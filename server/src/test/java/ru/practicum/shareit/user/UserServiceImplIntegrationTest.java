package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final UserService userService;

    @Test
    void getUsers() {
        final String nameUser1 = "user1";
        final String nameUser2 = "user2";
        final String emailUser1 = "user1@domain.com";
        final String emailUser2 = "user2@domain.com";

        UserResponseDto user1 = userService.createUser(new UserCreateDto(nameUser1, emailUser1));
        UserResponseDto user2 = userService.createUser(new UserCreateDto(nameUser2, emailUser2));

        List<UserResponseDto> expected = List.of(user1, user2);
        List<UserResponseDto> result = userService.getUsers();

        assertThat(result).isEqualTo(expected);
    }
}