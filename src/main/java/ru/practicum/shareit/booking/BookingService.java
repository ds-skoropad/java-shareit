package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(Integer userId, BookingCreateDto bookingCreateDto);

    BookingResponseDto updateBookingStatus(Integer userId, Integer bookingId, boolean approved);

    BookingResponseDto getBookingById(Integer userId, Integer bookingId);

    List<BookingResponseDto> getBookingsByUserId(Integer userId, String state);

    List<BookingResponseDto> getBookingsByOwnerId(Integer ownerId, String state);
}
