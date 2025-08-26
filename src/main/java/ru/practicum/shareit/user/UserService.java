package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Integer userId);

    UserResponseDto createUser(UserCreateDto userCreateDto);

    UserResponseDto updateUser(UserUpdateDto userUpdateDto);

    void deleteUserById(Integer userId);
}
