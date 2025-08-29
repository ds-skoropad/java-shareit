package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

public interface CommentService {
    CommentResponseDto createComment(Integer userId, CommentCreateDto commentCreateDto);
}
