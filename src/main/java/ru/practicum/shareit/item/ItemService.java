package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    List<ItemResponseDto> getItemsByUserId(Integer userId);

    List<ItemResponseDto> getItemsByText(Integer userId, String text);

    ItemResponseDto getItemById(Integer userId, Integer itemId);

    ItemResponseDto createItem(Integer userId, ItemCreateDto itemCreateDto);

    ItemResponseDto updateItem(Integer userId, ItemUpdateDto itemUpdateDto);

    CommentResponseDto createComment(Integer userId, Integer itemId, CommentCreateDto commentCreateDto);

}
