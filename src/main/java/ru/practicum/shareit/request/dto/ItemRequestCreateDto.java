package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.practicum.shareit.request.ItemRequestConstants;

public record ItemRequestCreateDto(
        @NotBlank
        @Size(min = ItemRequestConstants.DESCRIPTION_MIN_LENGTH, max = ItemRequestConstants.DESCRIPTION_MAX_LENGTH)
        String description
) {
}
