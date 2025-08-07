package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserCreateDto userCreateDto) {
        return new User(
                null,
                userCreateDto.name(),
                userCreateDto.email()
        );
    }

    public static void updateUser(User user, UserUpdateDto userUpdateDto) {
        user.setName(userUpdateDto.name() != null ? userUpdateDto.name() : user.getName());
        user.setEmail(userUpdateDto.email() != null ? userUpdateDto.email() : user.getEmail());
    }
}
