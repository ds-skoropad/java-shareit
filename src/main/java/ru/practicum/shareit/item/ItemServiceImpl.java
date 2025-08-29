package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
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

    @Override
    public List<ItemResponseDto> getItemsByUserId(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        List<Item> items = itemRepository.findByOwnerId(userId);
        if (items.isEmpty()) {
            return List.of();
        }
        List<Integer> itemIds = items.stream().map(Item::getId).toList();

        return ItemMapper.toListItemResponseDto(items, getCommentsMap(itemIds), getBookingsMap(itemIds));
    }

    @Override
    public List<ItemResponseDto> getItemsByText(Integer userId, String text) {
        if (text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.searchAvailableByText(text);
        if (items.isEmpty()) {
            return List.of();
        }
        List<Integer> itemIds = items.stream().map(Item::getId).toList();

        return text.isBlank() ? List.of() : ItemMapper.toListItemResponseDto(items, getCommentsMap(itemIds), null);
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
        Item createItem = itemRepository.save(ItemMapper.toItem(itemCreateDto, owner));
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
        updateItem = ItemMapper.patchItem(updateItem, itemUpdateDto);
        updateItem = itemRepository.save(updateItem);
        log.info("Update item: {}", updateItem);
        return ItemMapper.toItemResponseDto(updateItem, null, null, List.of());
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
}
