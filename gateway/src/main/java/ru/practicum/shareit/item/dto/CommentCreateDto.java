package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.practicum.shareit.item.CommentConstants;

public record CommentCreateDto(
        @NotBlank @Size(min = ru.practicum.shareit.item.CommentConstants.TEXT_MIN_LENGTH, max = CommentConstants.TEXT_MAX_LENGTH)
        String text
) {
}
