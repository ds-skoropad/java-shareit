package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemShortRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    private final Integer id = 1;
    private final String name = "test-name";
    private final String description = "test-description";
    private final String email = "test-email@domain.com";
    private final LocalDateTime localDateTime = LocalDateTime.now();

    @Test
    void toItemRequestResponseDto() {
        User user = new User(id, name, email);
        Item item = new Item(id, name, description, true, user, null);
        ItemRequest itemRequest = new ItemRequest(id, description, user, localDateTime, List.of(item));
        ItemRequestResponseDto expected = new ItemRequestResponseDto(
                id,
                description,
                localDateTime,
                List.of(new ItemShortRequestResponseDto(id, name, id)));
        ItemRequestResponseDto result = ItemRequestMapper.toItemRequestResponseDto(itemRequest);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toItemRequest() {
        User user = new User(id, name, email);
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto(description);
        ItemRequest result = ItemRequestMapper.toItemRequest(itemRequestCreateDto, user);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", null)
                .hasFieldOrPropertyWithValue("description", description)
                .hasFieldOrPropertyWithValue("requestor", user);
    }
}