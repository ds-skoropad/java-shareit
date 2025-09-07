package ru.practicum.shareit.item.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.practicum.shareit.item.ItemConstants;

public record ItemCreateDto(
        @NotBlank @Size(min = ItemConstants.NAME_MIN_LENGTH, max = ItemConstants.NAME_MAX_LENGTH)
        String name,
        @NotBlank @Size(min = ItemConstants.DESCRIPTION_MIN_LENGTH, max = ItemConstants.DESCRIPTION_MAX_LENGTH)
        String description,
        @NotNull
        Boolean available,
        @Nullable
        Integer requestId
) {
}
