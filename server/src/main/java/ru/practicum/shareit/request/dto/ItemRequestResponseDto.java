package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemShortRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record ItemRequestResponseDto(
        Integer id,
        String description,
        LocalDateTime created,
        List<ItemShortRequestResponseDto> items
) {
}
