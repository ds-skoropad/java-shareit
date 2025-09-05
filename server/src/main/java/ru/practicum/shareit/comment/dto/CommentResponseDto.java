package ru.practicum.shareit.comment.dto;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Integer id,
        String text,
        String authorName,
        LocalDateTime created
) {
}
