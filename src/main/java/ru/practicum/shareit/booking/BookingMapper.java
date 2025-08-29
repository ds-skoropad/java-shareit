package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemShortResponseDto(booking.getItem()),
                UserMapper.toUserResponseDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static BookingShortResponseDto toBookingShortResponseDto(Booking booking) {
        return booking == null ? null : new BookingShortResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}
