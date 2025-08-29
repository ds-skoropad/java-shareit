package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import ru.practicum.shareit.comment.CommentConstants;

public record CommentCreateDto(
        @Null
        Integer itemId,
        @NotBlank @Size(min = CommentConstants.TEXT_MIN_LENGTH, max = CommentConstants.TEXT_MAX_LENGTH)
        String text
) {
}
