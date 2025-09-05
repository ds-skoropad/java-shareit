package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final Integer id = 1;
    private final String name = "name";
    private final String description = "test-description";
    private final String email = "email@domain.com";
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingResponseDto expected;


    @BeforeEach
    void setUp() {
        user = new User(id, name, email);
        owner = new User(2, name, email);
        item = new Item(id, name, description, true, owner, new ItemRequest());
        booking = new Booking(id, localDateTime, localDateTime, item, user, BookingStatus.WAITING);
        expected = new BookingResponseDto(id, localDateTime, localDateTime,
                new ItemShortResponseDto(id, name), new UserResponseDto(id, name, email), BookingStatus.WAITING);
    }

    @Test
    void createBooking() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(id, localDateTime, localDateTime);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndDateRangeAndStateIn(anyInt(), any(), any(), anyList())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingResponseDto result = bookingService.createBooking(user.getId(), bookingCreateDto);

        assertThat(result).isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).existsByItemIdAndDateRangeAndStateIn(anyInt(), any(), any(), anyList());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBooking_whenStartAfterEnd_thenException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(id, localDateTime, localDateTime.minusDays(1));

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), bookingCreateDto))
                .isInstanceOf(NotValidException.class)
                .hasMessageContaining("The start date must be before the end date.");

        verify(userRepository, never()).findById(any());
        verify(itemRepository, never()).findById(any());
        verify(bookingRepository, never()).existsByItemIdAndDateRangeAndStateIn(anyInt(), any(), any(), anyList());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenUserIdNotFound_thenException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(id, localDateTime, localDateTime);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), bookingCreateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, never()).findById(any());
        verify(bookingRepository, never()).existsByItemIdAndDateRangeAndStateIn(anyInt(), any(), any(), anyList());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenItemIdNotFound_thenException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(id, localDateTime, localDateTime);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), bookingCreateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item not found");

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, never()).existsByItemIdAndDateRangeAndStateIn(anyInt(), any(), any(), anyList());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenItemNotAvailable_thenException() {
        item.setAvailable(false);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(id, localDateTime, localDateTime);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), bookingCreateDto))
                .isInstanceOf(NotValidException.class)
                .hasMessageContaining("Item not available for booking");

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, never()).existsByItemIdAndDateRangeAndStateIn(anyInt(), any(), any(), anyList());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenSameUserAndOwnerItem_thenException() {
        item.setOwner(user);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(id, localDateTime, localDateTime);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), bookingCreateDto))
                .isInstanceOf(NotValidException.class)
                .hasMessageContaining("Can't booking your item");

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, never()).existsByItemIdAndDateRangeAndStateIn(anyInt(), any(), any(), anyList());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenDateCrossing_thenException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(id, localDateTime, localDateTime);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndDateRangeAndStateIn(anyInt(), any(), any(), anyList())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), bookingCreateDto))
                .isInstanceOf(NotValidException.class)
                .hasMessageContaining("Booking time in busy range");

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).existsByItemIdAndDateRangeAndStateIn(anyInt(), any(), any(), anyList());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingStatus_whenApprovedTrue_thenStatusApproved() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        expected = new BookingResponseDto(id, localDateTime, localDateTime,
                new ItemShortResponseDto(id, name), new UserResponseDto(id, name, email), BookingStatus.APPROVED);
        BookingResponseDto result = bookingService.updateBookingStatus(owner.getId(), booking.getId(), true);


        assertThat(result).isEqualTo(expected);

        verify(bookingRepository, times(1)).findById(any());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void updateBookingStatus_whenApprovedFalse_thenStatusRejected() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        expected = new BookingResponseDto(id, localDateTime, localDateTime,
                new ItemShortResponseDto(id, name), new UserResponseDto(id, name, email), BookingStatus.REJECTED);
        BookingResponseDto result = bookingService.updateBookingStatus(owner.getId(), booking.getId(), false);


        assertThat(result).isEqualTo(expected);

        verify(bookingRepository, times(1)).findById(any());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void updateBookingStatus_whenBookingIdNotFound_thenException() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.updateBookingStatus(owner.getId(), booking.getId(), true))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Booking not found");

        verify(bookingRepository, times(1)).findById(any());
        verify(userRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingStatus_whenNotOwner_thenException() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));

        assertThatThrownBy(() -> bookingService.updateBookingStatus(user.getId(), booking.getId(), true))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Only owners allowed");

        verify(bookingRepository, times(1)).findById(any());
        verify(userRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingStatus_whenUserIdNotFound_thenException() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.updateBookingStatus(owner.getId(), booking.getId(), true))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(bookingRepository, times(1)).findById(any());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingStatus_whenStatusNotWaiting_thenException() {
        booking.setStatus(BookingStatus.CANCELED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.ofNullable(owner));

        assertThatThrownBy(() -> bookingService.updateBookingStatus(owner.getId(), booking.getId(), true))
                .isInstanceOf(NotValidException.class)
                .hasMessageContaining("Booking has already been processed");

        verify(bookingRepository, times(1)).findById(any());
        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingById() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingResponseDto result = bookingService.getBookingById(owner.getId(), booking.getId());

        assertThat(result).isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getBookingById_whenUserIdNotFound_thenException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(owner.getId(), booking.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, never()).findById(any());
    }

    @Test
    void getBookingById_whenBookerIdNotFound_thenException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(owner.getId(), booking.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Booking not found");

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getBookingById_whenNotOwner_thenException() {
        item.setOwner(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(user.getId(), booking.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Only owners allowed");

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findById(any());
    }

    @Test
    void getBookingsByUserId_whenUserIdNotFound_thenException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingsByUserId(user.getId(), BookingState.ALL))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, never()).findByBookerIdOrderByStartDesc(any());
    }

    @Test
    void getBookingsByUserId_whenStateAll() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdOrderByStartDesc(user.getId())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByUserId(user.getId(), BookingState.ALL);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(any());
    }

    @Test
    void getBookingsByUserId_whenStateCurrent() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByUserId(user.getId(), BookingState.CURRENT);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getBookingsByUserId_whenStatePast() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(any(), any())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByUserId(user.getId(), BookingState.PAST);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByBookerIdAndEndBeforeOrderByStartDesc(any(), any());
    }


    @Test
    void getBookingsByUserId_whenStateFuture() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(any(), any())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByUserId(user.getId(), BookingState.FUTURE);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByBookerIdAndStartAfterOrderByStartDesc(any(), any());
    }

    @Test
    void getBookingsByUserId_whenStateWaiting() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByUserId(user.getId(), BookingState.WAITING);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByBookerIdAndStatusOrderByStartDesc(any(), any());
    }

    @Test
    void getBookingsByUserId_whenStateRejected() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByUserId(user.getId(), BookingState.REJECTED);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByBookerIdAndStatusOrderByStartDesc(any(), any());
    }


    @Test
    void getBookingsByOwnerId_whenUserIdNotFound_thenException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingsByOwnerId(owner.getId(), BookingState.ALL))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, never()).findByItemOwnerIdOrderByStartDesc(any());
    }

    @Test
    void getBookingsByOwnerId_whenStateAll() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByOwnerId(owner.getId(), BookingState.ALL);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByItemOwnerIdOrderByStartDesc(any());
    }

    @Test
    void getBookingsByOwnerId_whenStateCurrent() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByOwnerId(owner.getId(), BookingState.CURRENT);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getBookingsByOwnerId_whenStatePast() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(any(), any())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByOwnerId(owner.getId(), BookingState.PAST);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndEndBeforeOrderByStartDesc(any(), any());
    }


    @Test
    void getBookingsByOwnerId_whenStateFuture() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByOwnerId(owner.getId(), BookingState.FUTURE);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any());
    }

    @Test
    void getBookingsByOwnerId_whenStateWaiting() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByOwnerId(owner.getId(), BookingState.WAITING);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStatusOrderByStartDesc(any(), any());
    }

    @Test
    void getBookingsByOwnerId_whenStateRejected() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of(booking));
        List<BookingResponseDto> result = bookingService.getBookingsByOwnerId(owner.getId(), BookingState.REJECTED);

        assertThat(result).first().isEqualTo(expected);

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStatusOrderByStartDesc(any(), any());
    }
}