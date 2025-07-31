package ru.practicum.shareit.item.dto;

public record ItemResponseDto(
        Integer id,
        String name,
        String description,
        Boolean available
) {
}
