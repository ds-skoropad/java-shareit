package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private final Integer id = 1;
    private final String name = "test-name";
    private final String description = "test-description";
    private final String email = "test-email@domain.com";
    private final LocalDateTime localDateTime = LocalDateTime.of(2025, 1, 1, 12, 0);

    private User user;
    private Item item;


    @BeforeEach
    void setUp() {
        user = new User(id, name, email);
        item = new Item(id, name, description, true, user, new ItemRequest());
    }

    @Test
    void toBooking() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(id, localDateTime, localDateTime);
        Booking expected = new Booking(null, localDateTime, localDateTime, item, user, BookingStatus.WAITING);
        Booking result = BookingMapper.toBooking(bookingCreateDto, item, user);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toBookingResponseDto() {
        Booking booking = new Booking(id, localDateTime, localDateTime, item, user, BookingStatus.WAITING);
        BookingResponseDto expected = new BookingResponseDto(id, localDateTime, localDateTime,
                new ItemShortResponseDto(id, name), new UserResponseDto(id, name, email), BookingStatus.WAITING);
        BookingResponseDto result = BookingMapper.toBookingResponseDto(booking);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toBookingShortResponseDto() {
        Booking booking = new Booking(id, localDateTime, localDateTime, item, user, BookingStatus.WAITING);
        BookingShortResponseDto expected = new BookingShortResponseDto(id, localDateTime, localDateTime);
        BookingShortResponseDto result = BookingMapper.toBookingShortResponseDto(booking);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toBookingShortResponseDto_whenBookingNull_thenNull() {
        BookingShortResponseDto expected = null;
        BookingShortResponseDto result = BookingMapper.toBookingShortResponseDto(null);

        assertThat(result).isEqualTo(expected);
    }
}