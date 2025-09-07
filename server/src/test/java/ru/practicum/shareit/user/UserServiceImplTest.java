package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final Integer id = 1;
    private final String name = "name";
    private final String email = "email@domain.com";
    private final UserResponseDto expectedUserResponseDto = new UserResponseDto(id, name, email);
    private User expectedUser;

    @BeforeEach
    void setUp() {
        expectedUser = new User(id, name, email);
    }

    @Test
    void getUsers() {
        when(userRepository.findAll()).thenReturn(List.of(expectedUser));
        List<UserResponseDto> users = userService.getUsers();

        assertThat(users)
                .hasSize(1)
                .first()
                .isEqualTo(expectedUserResponseDto);
        verify(userRepository).findAll();
    }

    @Test
    void getUserById() {
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(expectedUser));
        UserResponseDto userResponseDto = userService.getUserById(id);

        assertThat(userResponseDto)
                .isEqualTo(expectedUserResponseDto);
        verify(userRepository).findById(anyInt());
    }

    @Test
    void getUserById_whenUserIdNotFound_thenException() {
        Integer notFoundUserId = id + 100;
        when(userRepository.findById(notFoundUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(notFoundUserId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository).findById(anyInt());
    }

    @Test
    void createUser() {
        UserCreateDto userCreateDto = new UserCreateDto(name, email);
        User createUser = new User(null, name, email);
        when(userRepository.save(createUser)).thenReturn(expectedUser);
        UserResponseDto userResponseDto = userService.createUser(userCreateDto);

        assertThat(userResponseDto)
                .isEqualTo(expectedUserResponseDto);
        verify(userRepository).save(any());
    }

    @Test
    void updateUser() {
        UserUpdateDto userUpdateDto = new UserUpdateDto(id, name, email);
        User updateUser = new User(id, name, email);
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(expectedUser));
        when(userRepository.save(updateUser)).thenReturn(expectedUser);
        UserResponseDto userResponseDto = userService.updateUser(userUpdateDto);

        assertThat(userResponseDto)
                .isEqualTo(expectedUserResponseDto);
        verify(userRepository).findById(anyInt());
        verify(userRepository).save(any());
    }

    @Test
    void updateUser_whenUserIdNotFound_thenException() {
        Integer notFoundUserId = id + 100;
        UserUpdateDto userUpdateDto = new UserUpdateDto(notFoundUserId, name, email);
        when(userRepository.findById(notFoundUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userUpdateDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository).findById(anyInt());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUserById() {
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(expectedUser));
        doNothing().when(userRepository).deleteById(id);

        userService.deleteUserById(id);
        verify(userRepository).findById(anyInt());
        verify(userRepository).deleteById(any());
    }

    @Test
    void deleteUserById_whenUserIdNotFound_thenException() {
        Integer notFoundUserId = id + 100;
        when(userRepository.findById(notFoundUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserById(notFoundUserId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository).findById(anyInt());
        verify(userRepository, never()).deleteById(any());
    }
}