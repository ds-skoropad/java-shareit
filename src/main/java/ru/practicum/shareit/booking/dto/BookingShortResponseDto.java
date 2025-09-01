package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public record BookingShortResponseDto(
        Integer id,
        LocalDateTime start,
        LocalDateTime end
) {
}
