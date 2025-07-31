package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import ru.practicum.shareit.item.ItemConstants;
import ru.practicum.shareit.valid.NullableNotBlank;

public record ItemUpdateDto(
        @Null
        Integer id,
        @NullableNotBlank @Size(min = ItemConstants.NAME_MIN_LENGTH, max = ItemConstants.NAME_MAX_LENGTH)
        String name,
        @NullableNotBlank @Size(min = ItemConstants.DESCRIPTION_MIN_LENGTH, max = ItemConstants.DESCRIPTION_MAX_LENGTH)
        String description,
        Boolean available
) {
}
