package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    List<ItemResponseDto> getAllItemsByUserId(Integer userId);

    List<ItemResponseDto> getAllItemsByText(Integer userId, String text);

    ItemResponseDto getItemById(Integer userId, Integer itemId);

    ItemResponseDto createItem(Integer userId, ItemCreateDto itemCreateDto);

    ItemResponseDto updateItem(Integer userId, ItemUpdateDto itemUpdateDto);

    void deleteItemById(Integer userId, Integer itemId);
}
