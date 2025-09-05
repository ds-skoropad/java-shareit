package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.assertj.core.api.Assertions.assertThat;


class UserMapperTest {

    private final Integer id = 1;
    private final String name = "name";
    private final String email = "email";

    @Test
    void toUserResponseDto() {
        User user = new User(id, name, email);
        UserResponseDto expectedUserResponseDto = new UserResponseDto(id, name, email);
        UserResponseDto mapUserResponseDto = UserMapper.toUserResponseDto(user);

        assertThat(mapUserResponseDto).isEqualTo(expectedUserResponseDto);
    }

    @Test
    void toUser() {
        UserCreateDto userCreateDto = new UserCreateDto(name, email);
        User expectedUser = new User(null, name, email);
        User mapUser = UserMapper.toUser(userCreateDto);

        assertThat(mapUser).isEqualTo(expectedUser);
    }

    @Test
    void updateUser_whenFieldNotNull_thenUpdateField() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(id, name, email);
        User expectedUser = new User(id, name, email);
        User updateUser = new User(id, "old-name", "old-email");
        UserMapper.updateUser(updateUser, userUpdateDto);

        assertThat(updateUser).isEqualTo(expectedUser);
    }

    @Test
    void updateUser_whenFieldNull_thenNotUpdateField() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(id, null, null);
        User expectedUser = new User(id, name, email);
        User updateUser = new User(id, name, email);
        UserMapper.updateUser(updateUser, userUpdateDto);

        assertThat(updateUser).isEqualTo(expectedUser);
    }

}