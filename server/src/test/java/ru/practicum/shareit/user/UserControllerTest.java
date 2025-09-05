package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final Integer id = 1;
    private final String name = "test-name";
    private final String email = "test-email@domain.com";
    private final UserCreateDto userCreateDto = new UserCreateDto(name, email);
    private final UserUpdateDto userUpdateDto = new UserUpdateDto(id, name, email);
    private final UserResponseDto userResponseDto = new UserResponseDto(id, name, email);

    @Test
    void getAllUsers() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(userResponseDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(id), Integer.class))
                .andExpect(jsonPath("$[0].name", is(name)))
                .andExpect(jsonPath("$[0].email", is(email)));
        verify(userService, times(1)).getUsers();
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(id)).thenReturn(userResponseDto);

        mvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id), Integer.class))
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.email", is(email)));
        verify(userService, times(1)).getUserById(anyInt());
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(userCreateDto)).thenReturn(userResponseDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id), Integer.class))
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.email", is(email)));
        verify(userService, times(1)).createUser(any());
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(userUpdateDto)).thenReturn(userResponseDto);

        mvc.perform(patch("/users/" + id)
                        .content(mapper.writeValueAsString(userUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id), Integer.class))
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.email", is(email)));
        verify(userService, times(1)).updateUser(any());
    }

    @Test
    void deleteUserById() throws Exception {
        doNothing().when(userService).deleteUserById(id);

        mvc.perform(delete("/users/" + id))
                .andExpect(status().isNoContent());
        verify(userService, times(1)).deleteUserById(anyInt());
    }
}