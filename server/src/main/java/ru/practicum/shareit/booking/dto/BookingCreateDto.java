package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public record BookingCreateDto(
        Integer itemId,
        LocalDateTime start,
        LocalDateTime end
) {
}
