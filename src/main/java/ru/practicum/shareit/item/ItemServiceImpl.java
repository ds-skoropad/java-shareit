package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemResponseDto> getItemsByUserId(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        return getListItemResponseDto(itemRepository.findByOwnerId(userId), true, true);
    }

    @Override
    public List<ItemResponseDto> getItemsByText(Integer userId, String text) {
        return text.isBlank() ?
                List.of() : getListItemResponseDto(itemRepository.searchAvailableByText(text), true, false);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getItemById(Integer userId, Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item not found: id = %d", itemId)));

        Booking lastBooking = null;
        Booking nextBooking = null;
        List<Comment> comments = commentRepository.findByItemId(itemId);

        if (userId.equals(item.getOwner().getId())) {
            lastBooking = getLastBooking(itemId);
            nextBooking = getNextBooking(itemId);
        }

        return ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public ItemResponseDto createItem(Integer userId, ItemCreateDto itemCreateDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        Integer requestId = itemCreateDto.requestId();
        ItemRequest itemRequest = requestId == null ? null : itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("ItemRequest not found: id = %d", requestId)));
        Item createItem = itemRepository.save(ItemMapper.toItem(itemCreateDto, owner, itemRequest));
        log.info("Create item: {}", createItem);
        return ItemMapper.toItemResponseDto(createItem, null, null, List.of());
    }

    @Override
    public ItemResponseDto updateItem(Integer userId, ItemUpdateDto itemUpdateDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        Item updateItem = itemRepository.findById(itemUpdateDto.id())
                .orElseThrow(() -> new NotFoundException(String.format("Item not found: id = %d", itemUpdateDto.id())));
        if (!userId.equals(updateItem.getOwner().getId())) {
            throw new ForbiddenException("Only owners allowed");
        }
        Integer requestId = itemUpdateDto.requestId();
        ItemRequest itemRequest = requestId == null ? null : itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("ItemRequest not found: id = %d", requestId)));
        updateItem = ItemMapper.patchItem(updateItem, itemUpdateDto, itemRequest);
        updateItem = itemRepository.save(updateItem);
        log.info("Update item: {}", updateItem);
        return ItemMapper.toItemResponseDto(updateItem, null, null, List.of());
    }

    @Override
    public CommentResponseDto createComment(Integer userId, Integer itemId, CommentCreateDto commentCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item not found: id = %d", itemId)));

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
            throw new ValidationException("User not booking or booking not completed");
        }

        Comment comment = CommentMapper.toComment(commentCreateDto, item, user);

        Comment createComment = commentRepository.save(comment);
        log.info("Create comment: {}", createComment);

        return CommentMapper.toCommentResponseDto(createComment);
    }

    // Additional methods

    private Booking getLastBooking(Integer itemId) {
        return bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now())
                .orElse(null);
    }

    private Booking getNextBooking(Integer itemId) {
        return bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now())
                .orElse(null);
    }

    private Map<Integer, List<Comment>> getCommentsMap(List<Integer> itemIds) {
        return commentRepository.findByItemIdIn(itemIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
    }

    private Map<Integer, List<Booking>> getBookingsMap(List<Integer> itemIds) {
        return bookingRepository.findByItemIdInAndStatus(itemIds, BookingStatus.APPROVED).stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
    }

    private List<ItemResponseDto> getListItemResponseDto(List<Item> items, boolean addComment, boolean addBooking) {
        if (items.isEmpty()) return List.of();

        List<Integer> itemIds = items.stream().map(Item::getId).toList();
        Map<Integer, List<Booking>> bookingMap = addComment ? getBookingsMap(itemIds) : null;
        Map<Integer, List<Comment>> commentsMap = addBooking ? getCommentsMap(itemIds) : null;

        return items.stream()
                .map(item -> {
                    Integer itemId = item.getId();
                    Booking lastBooking = null;
                    Booking nextBooking = null;

                    if (bookingMap != null) {
                        LocalDateTime now = LocalDateTime.now();
                        List<Booking> bookings = bookingMap.getOrDefault(itemId, List.of());
                        lastBooking = bookings.stream()
                                .filter(b -> b.getEnd().isBefore(now))
                                .max(Comparator.comparing(Booking::getEnd))
                                .orElse(null);
                        nextBooking = bookings.stream()
                                .filter(b -> b.getStart().isAfter(now))
                                .min(Comparator.comparing(Booking::getStart))
                                .orElse(null);
                    }

                    return ItemMapper.toItemResponseDto(
                            item,
                            lastBooking,
                            nextBooking,
                            commentsMap == null ? List.of() : commentsMap.getOrDefault(itemId, List.of())
                    );
                })
                .toList();
    }
}
