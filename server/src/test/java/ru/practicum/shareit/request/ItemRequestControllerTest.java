package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemShortRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.ShareItConstants.REQ_HEAD_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final Integer userId = 1;
    private final Integer itemId = 1;
    private final String itemName = "item-name";
    private final ItemShortRequestResponseDto itemShortRequestResponseDto = new ItemShortRequestResponseDto(
            itemId, itemName, userId);

    private final Integer id = 1;
    private final String description = "test-description";
    private final ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(
            id, description, LocalDateTime.now(), List.of(itemShortRequestResponseDto));

    @Test
    void createItemRequest() throws Exception {
        final ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto(description);
        when(itemRequestService.createItemRequest(userId, itemRequestCreateDto)).thenReturn(itemRequestResponseDto);

        mvc.perform(post("/requests")
                        .header(REQ_HEAD_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id), Integer.class))
                .andExpect(jsonPath("$.description", is(description)))
                .andExpect(jsonPath("$.items[0].id", is(itemId)))
                .andExpect(jsonPath("$.items[0].name", is(itemName)));
        verify(itemRequestService).createItemRequest(anyInt(), any());
    }

    @Test
    void getItemRequestsByUserId() throws Exception {
        when(itemRequestService.getItemRequestsByUserId(userId)).thenReturn(List.of(itemRequestResponseDto));

        mvc.perform(get("/requests")
                        .header(REQ_HEAD_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(id), Integer.class))
                .andExpect(jsonPath("$[0].description", is(description)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemId)))
                .andExpect(jsonPath("$[0].items[0].name", is(itemName)));
        verify(itemRequestService).getItemRequestsByUserId(anyInt());
    }

    @Test
    void getItemRequests() throws Exception {
        when(itemRequestService.getItemRequests(userId)).thenReturn(List.of(itemRequestResponseDto));

        mvc.perform(get("/requests/all")
                        .header(REQ_HEAD_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(id), Integer.class))
                .andExpect(jsonPath("$[0].description", is(description)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemId)))
                .andExpect(jsonPath("$[0].items[0].name", is(itemName)));
        verify(itemRequestService).getItemRequests(anyInt());
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(userId, id)).thenReturn(itemRequestResponseDto);

        mvc.perform(get("/requests/" + id)
                        .header(REQ_HEAD_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id), Integer.class))
                .andExpect(jsonPath("$.description", is(description)))
                .andExpect(jsonPath("$.items[0].id", is(itemId)))
                .andExpect(jsonPath("$.items[0].name", is(itemName)));
        verify(itemRequestService).getItemRequestById(anyInt(), anyInt());
    }
}