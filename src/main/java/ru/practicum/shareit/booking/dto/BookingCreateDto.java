package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingCreateDto(
        @NotNull
        Integer itemId,
        @NotNull
        @Future
        LocalDateTime start,
        @NotNull
        @Future
        LocalDateTime end
) {
}
