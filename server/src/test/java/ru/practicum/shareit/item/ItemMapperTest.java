package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private final Integer id = 1;
    private final String name = "test-name";
    private final String description = "test-description";
    private final String email = "test-email@domain.com";
    private final LocalDateTime localDateTime = LocalDateTime.now();

    private User user;
    private Item item;
    private ItemRequest itemRequest;


    @BeforeEach
    void setUp() {
        user = new User(id, name, email);
        item = new Item(id, name, description, true, user, new ItemRequest(id, description, user, localDateTime, List.of()));
        itemRequest = new ItemRequest(id, description, user, localDateTime, List.of(item));
    }

    @Test
    void toItemShortRequestResponseDto() {
        ItemShortRequestResponseDto expected = new ItemShortRequestResponseDto(id, name, user.getId());
        ItemShortRequestResponseDto result = ItemMapper.toItemShortRequestResponseDto(item);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toItemShortResponseDto() {
        ItemShortResponseDto expected = new ItemShortResponseDto(id, name);
        ItemShortResponseDto result = ItemMapper.toItemShortResponseDto(item);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void toItemResponseDto_whenItemNull_thenNotGetRequest() {
        Booking booking = new Booking(id, localDateTime, localDateTime, new Item(), new User(), BookingStatus.WAITING);
        Comment comment = new Comment(id, description, new Item(), new User(), localDateTime);
        Item itemNullRequest = new Item(id, name, description, true, user, null);
        ItemResponseDto result = ItemMapper.toItemResponseDto(itemNullRequest, booking, booking, List.of(comment));

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("description", description)
                .hasFieldOrPropertyWithValue("requestId", null);
    }

    @Test
    void toItemResponseDto_whenItemNotNull_thenGetRequest() {
        Booking booking = new Booking(id, localDateTime, localDateTime, new Item(), new User(), BookingStatus.WAITING);
        Comment comment = new Comment(id, description, new Item(), new User(), localDateTime);
        ItemResponseDto result = ItemMapper.toItemResponseDto(item, booking, booking, List.of(comment));

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("description", description)
                .hasFieldOrPropertyWithValue("requestId", id);
    }

    @Test
    void toItem() {
        ItemCreateDto itemCreateDto = new ItemCreateDto(name, description, true, id);
        Item expected = new Item(null, name, description, true, user, itemRequest);
        Item result = ItemMapper.toItem(itemCreateDto, user, itemRequest);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void patchItem_whenNotNull_thenUpdateFields() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(id, name, description, true, id);
        Item patchItem = new Item(id, "none", "none", false, user, itemRequest);
        Item result = ItemMapper.patchItem(patchItem, itemUpdateDto, itemRequest);

        assertThat(result)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("description", description)
                .hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    void patchItem_whenNull_thenNotUpdateFields() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(id, null, null, null, id);
        Item patchItem = new Item(id, name, description, true, user, itemRequest);
        Item result = ItemMapper.patchItem(patchItem, itemUpdateDto, itemRequest);

        assertThat(result)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("description", description)
                .hasFieldOrPropertyWithValue("available", true);
    }
}