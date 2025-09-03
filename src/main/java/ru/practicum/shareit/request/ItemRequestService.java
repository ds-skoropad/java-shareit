package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto createItemRequest(Integer userId, ItemRequestCreateDto itemRequestCreateDto);

    List<ItemRequestResponseDto> getItemRequestsByUserId(Integer userId);

    List<ItemRequestResponseDto> getItemRequests(Integer userId);

    ItemRequestResponseDto getItemRequestById(Integer userId, Integer requestId);
}
