package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ShareItConstants.REQ_HEAD_USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final Integer id = 1;
    private final Integer userId = 1;
    private final String name = "test-name";
    private final String description = "test-description";
    private final ItemResponseDto itemResponseDto = new ItemResponseDto(id, name, description, true, id, null, null, null, id);

    private final String text = "test-text";
    private final CommentResponseDto commentResponseDto = new CommentResponseDto(id, text, name, LocalDateTime.now());

    @Test
    void getItemsByUserId() throws Exception {
        when(itemService.getItemsByUserId(userId)).thenReturn(List.of(itemResponseDto));

        mvc.perform(get("/items")
                        .header(REQ_HEAD_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto.name())))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto.description())));
        verify(itemService, times(1)).getItemsByUserId(anyInt());
    }

    @Test
    void getItemsByText() throws Exception {
        final String param = "test-param";
        when(itemService.getItemsByText(userId, param)).thenReturn(List.of(itemResponseDto));

        mvc.perform(get("/items/search")
                        .header(REQ_HEAD_USER_ID, userId)
                        .param("text", param))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto.name())))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto.description())));
        verify(itemService, times(1)).getItemsByText(anyInt(), anyString());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(userId, id)).thenReturn(itemResponseDto);

        mvc.perform(get("/items/" + id)
                        .header(REQ_HEAD_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.name())))
                .andExpect(jsonPath("$.description", is(itemResponseDto.description())));
        verify(itemService, times(1)).getItemById(anyInt(), anyInt());
    }

    @Test
    void createItem() throws Exception {
        final ItemCreateDto itemCreateDto = new ItemCreateDto(name, description, true, id);
        when(itemService.createItem(userId, itemCreateDto)).thenReturn(itemResponseDto);

        mvc.perform(post("/items")
                        .header(REQ_HEAD_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.name())))
                .andExpect(jsonPath("$.description", is(itemResponseDto.description())));
        verify(itemService, times(1)).createItem(anyInt(), any());
    }

    @Test
    void updateItem() throws Exception {
        final ItemUpdateDto itemUpdateDto = new ItemUpdateDto(id, name, description, true, id);
        when(itemService.updateItem(userId, itemUpdateDto)).thenReturn(itemResponseDto);

        mvc.perform(patch("/items/" + itemUpdateDto.id())
                        .header(REQ_HEAD_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemUpdateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.name())))
                .andExpect(jsonPath("$.description", is(itemResponseDto.description())));
        verify(itemService, times(1)).updateItem(anyInt(), any());
    }

    @Test
    void createComment() throws Exception {
        final CommentCreateDto commentCreateDto = new CommentCreateDto(text);
        when(itemService.createComment(userId, id, commentCreateDto)).thenReturn(commentResponseDto);

        mvc.perform(post("/items/" + id + "/comment")
                        .header(REQ_HEAD_USER_ID, userId)
                        .content(mapper.writeValueAsString(commentCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(commentResponseDto.id()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentResponseDto.text())))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.authorName())));
        verify(itemService, times(1)).createComment(anyInt(), anyInt(), any());
    }
}