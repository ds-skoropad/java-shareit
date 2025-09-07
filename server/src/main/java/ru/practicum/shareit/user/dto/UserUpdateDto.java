package ru.practicum.shareit.user.dto;

public record UserUpdateDto(
        Integer id,
        String name,
        String email
) {
}
