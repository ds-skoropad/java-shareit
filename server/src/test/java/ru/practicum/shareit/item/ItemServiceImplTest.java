package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final Integer id = 1;
    private final String name = "name";
    private final String description = "test-description";
    private final String email = "email@domain.com";
    private final LocalDateTime localDateTime = LocalDateTime.of(2025, 1, 1, 12, 0);
    private User user;
    private User owner;
    private Item item;
    private Item itemFull;
    private Comment comment;
    private ItemRequest itemRequest;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private CommentCreateDto commentCreateDto;

    private ItemResponseDto expectedItemResponse;
    private CommentResponseDto expectedCommentResponse;


    @BeforeEach
    void setUp() {
        user = new User(id, name, email);
        owner = new User(2, name, email);
        item = new Item(id, name, description, true, owner, null);
        itemRequest = new ItemRequest(id, description, user, localDateTime, List.of(item));
        itemFull = new Item(id, name, description, true, owner, itemRequest);
        comment = new Comment(id, description, item, user, localDateTime);

        itemCreateDto = new ItemCreateDto(name, description, true, id);
        itemUpdateDto = new ItemUpdateDto(id, name, description, true, id);
        commentCreateDto = new CommentCreateDto(description);

        BookingShortResponseDto bookingShort = new BookingShortResponseDto(id, localDateTime, localDateTime);
        CommentResponseDto commentResponseDto = new CommentResponseDto(id, description, name, localDateTime);
        expectedItemResponse = new ItemResponseDto(id, name, description, true, owner.getId(),
                null, null, List.of(), id);
        expectedCommentResponse = new CommentResponseDto(id, description, name, localDateTime);
    }

    @Test
    void getItemsByUserId() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(owner.getId())).thenReturn(List.of(itemFull));
        List<ItemResponseDto> result = itemService.getItemsByUserId(owner.getId());

        assertThat(result).first().isEqualTo(expectedItemResponse);

        verify(userRepository).findById(any());
        verify(itemRepository).findByOwnerId(any());
    }

    @Test
    void getItemsByUserId_whenUserIdNotFound_thenException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemsByUserId(owner.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(any());
        verify(itemRepository, never()).findByOwnerId(any());
    }

    @Test
    void getItemsByText() {
        when(itemRepository.searchAvailableByText(anyString())).thenReturn(List.of(itemFull));
        List<ItemResponseDto> result = itemService.getItemsByText(owner.getId(), "text");

        assertThat(result).first().isEqualTo(expectedItemResponse);

        verify(itemRepository).searchAvailableByText(any());
    }

    @Test
    void getItemsByText_whenTextIsBlank_thenEmptyList() {
        List<ItemResponseDto> result = itemService.getItemsByText(owner.getId(), "");

        assertThat(result).isEmpty();

        verify(itemRepository, never()).searchAvailableByText(any());
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(itemFull));
        when(commentRepository.findByItemId(anyInt())).thenReturn(List.of(comment));
        expectedItemResponse = new ItemResponseDto(id, name, description, true, owner.getId(),
                null, null, List.of(expectedCommentResponse), id);
        ItemResponseDto result = itemService.getItemById(owner.getId(), itemFull.getId());

        assertThat(result).isEqualTo(expectedItemResponse);

        verify(itemRepository).findById(any());
        verify(commentRepository).findByItemId(any());
    }

    @Test
    void getItemById_whenItemNotFound_thenException() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemById(owner.getId(), itemFull.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item not found");

        verify(itemRepository).findById(any());
        verify(commentRepository, never()).findByItemId(any());
    }

    @Test
    void createItem() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(itemFull);
        ItemResponseDto result = itemService.createItem(user.getId(), itemCreateDto);

        assertThat(result).isEqualTo(expectedItemResponse);

        verify(userRepository).findById(any());
        verify(itemRequestRepository).findById(any());
        verify(itemRepository).save(any());
    }

    @Test
    void createItem_whenUserIdNotFound_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(user.getId(), itemCreateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(any());
        verify(itemRequestRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void createItem_whenItemRequestIdNotFound_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(user.getId(), itemCreateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ItemRequest not found");

        verify(userRepository).findById(any());
        verify(itemRequestRepository).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void createItem_whenItemRequestNull() {
        itemCreateDto = new ItemCreateDto(name, description, true, null);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);
        expectedItemResponse = new ItemResponseDto(id, name, description, true, owner.getId(),
                null, null, List.of(), null);
        ItemResponseDto result = itemService.createItem(user.getId(), itemCreateDto);

        assertThat(result).isEqualTo(expectedItemResponse);

        verify(userRepository).findById(any());
        verify(itemRequestRepository, never()).findById(any());
        verify(itemRepository).save(any());
    }

    @Test
    void updateItem() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(itemFull));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(itemFull);
        ItemResponseDto result = itemService.updateItem(owner.getId(), itemUpdateDto);

        assertThat(result).isEqualTo(expectedItemResponse);

        verify(userRepository).findById(any());
        verify(itemRepository).findById(any());
        verify(itemRequestRepository).findById(any());
        verify(itemRepository).save(any());
    }

    @Test
    void updateItem_whenUserIdNotFound_thenException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(owner.getId(), itemUpdateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(any());
        verify(itemRepository, never()).findById(any());
        verify(itemRequestRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_whenItemIdNotFound_thenException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(owner.getId(), itemUpdateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item not found");

        verify(userRepository).findById(any());
        verify(itemRepository).findById(any());
        verify(itemRequestRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_whenItemRequestIdNotFound_thenException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(itemFull));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(owner.getId(), itemUpdateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ItemRequest not found");

        verify(userRepository).findById(any());
        verify(itemRepository).findById(any());
        verify(itemRequestRepository).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_whenNotOwner_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(itemFull));

        assertThatThrownBy(() -> itemService.updateItem(user.getId(), itemUpdateDto))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Only owners allowed");

        verify(userRepository).findById(any());
        verify(itemRepository).findById(any());
        verify(itemRequestRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void createComment() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyInt(), anyInt(), any())).thenReturn(true);
        when(commentRepository.save(any())).thenReturn(comment);
        CommentResponseDto result = itemService.createComment(user.getId(), item.getId(), commentCreateDto);

        assertThat(result).isEqualTo(expectedCommentResponse);

        verify(userRepository).findById(any());
        verify(itemRepository).findById(any());
        verify(bookingRepository).existsByBookerIdAndItemIdAndEndBefore(anyInt(), anyInt(), any());
        verify(commentRepository).save(any());
    }

    @Test
    void createComment_whenUserIdNotFound_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createComment(user.getId(), item.getId(), commentCreateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(any());
        verify(itemRepository, never()).findById(any());
        verify(bookingRepository, never()).existsByBookerIdAndItemIdAndEndBefore(anyInt(), anyInt(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenItemIdNotFound_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createComment(user.getId(), item.getId(), commentCreateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item not found");

        verify(userRepository).findById(any());
        verify(itemRepository).findById(any());
        verify(bookingRepository, never()).existsByBookerIdAndItemIdAndEndBefore(anyInt(), anyInt(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenBookerExists_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyInt(), anyInt(), any())).thenReturn(false);

        assertThatThrownBy(() -> itemService.createComment(user.getId(), item.getId(), commentCreateDto))
                .isInstanceOf(NotValidException.class)
                .hasMessageContaining("User not booking or booking not completed");

        verify(userRepository).findById(any());
        verify(itemRepository).findById(any());
        verify(bookingRepository).existsByBookerIdAndItemIdAndEndBefore(anyInt(), anyInt(), any());
        verify(commentRepository, never()).save(any());
    }
}