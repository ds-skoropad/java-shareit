package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.util.List;

public record ItemResponseDto(
        Integer id,
        String name,
        String description,
        Boolean available,
        Integer ownerId,
        BookingShortResponseDto lastBooking,
        BookingShortResponseDto nextBooking,
        List<CommentResponseDto> comments,
        Integer requestId
) {
}
