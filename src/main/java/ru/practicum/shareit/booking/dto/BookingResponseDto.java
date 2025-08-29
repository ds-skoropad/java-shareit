package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

public record BookingResponseDto(
        Integer id,
        LocalDateTime start,
        LocalDateTime end,
        ItemShortResponseDto item,
        UserResponseDto booker,
        BookingStatus status
) {
}
