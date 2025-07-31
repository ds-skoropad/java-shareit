package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import ru.practicum.shareit.user.UserConstants;
import ru.practicum.shareit.valid.NullableNotBlank;

public record UserUpdateDto(
        @Null
        Integer id,
        @NullableNotBlank @Size(min = UserConstants.NAME_SIZE_MIN, max = UserConstants.NAME_SIZE_MAX)
        String name,
        @Email @Size(max = UserConstants.EMAIL_SIZE_MAX)
        String email
) {
}
