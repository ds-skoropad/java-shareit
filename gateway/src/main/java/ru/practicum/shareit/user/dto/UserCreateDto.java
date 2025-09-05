package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.practicum.shareit.user.UserConstants;

public record UserCreateDto(
        @NotBlank @Size(min = UserConstants.NAME_SIZE_MIN, max = UserConstants.NAME_SIZE_MAX)
        String name,
        @NotNull @Email @Size(max = UserConstants.EMAIL_SIZE_MAX)
        String email
) {
}
