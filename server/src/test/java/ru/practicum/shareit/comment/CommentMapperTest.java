package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    private final Integer id = 1;
    private final String name = "test-name";
    private final String description = "test-description";
    private final String email = "test-email@domain.com";
    private final LocalDateTime localDateTime = LocalDateTime.of(2025, 1, 1, 12, 0);

    private User user;
    private Item item;


    @BeforeEach
    void setUp() {
        user = new User(id, name, email);
        item = new Item(id, name, description, true, user, new ItemRequest());
    }

    @Test
    void toComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto(description);
        Comment result = CommentMapper.toComment(commentCreateDto, item, user);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", null)
                .hasFieldOrPropertyWithValue("text", description)
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("author", user);
    }

    @Test
    void toCommentResponseDto() {
        Comment comment = new Comment(id, description, item, user, localDateTime);
        CommentResponseDto expected = new CommentResponseDto(id, description, name, localDateTime);
        CommentResponseDto result = CommentMapper.toCommentResponseDto(comment);

        assertThat(result).isEqualTo(expected);
    }
}