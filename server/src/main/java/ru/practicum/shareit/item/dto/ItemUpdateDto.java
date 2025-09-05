package ru.practicum.shareit.item.dto;

public record ItemUpdateDto(
        Integer id,
        String name,
        String description,
        Boolean available,
        Integer requestId
) {
}
