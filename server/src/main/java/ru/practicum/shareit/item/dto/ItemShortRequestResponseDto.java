package ru.practicum.shareit.item.dto;

public record ItemShortRequestResponseDto(
        Integer id,
        String name,
        Integer ownerId
) {
}
