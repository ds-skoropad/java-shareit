package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestResponseDto createItemRequest(Integer userId, ItemRequestCreateDto itemRequestCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        ItemRequest createItemRequest =
                itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestCreateDto, user));
        log.info("Create itemRequest: {}", createItemRequest);
        return ItemRequestMapper.toItemRequestResponseDto(createItemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getItemRequestsByUserId(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getItemRequests(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponseDto getItemRequestById(Integer userId, Integer requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        return itemRequestRepository.findById(requestId)
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .orElseThrow(() -> new NotFoundException(String.format("ItemRequest not found: id = %d", requestId)));
    }
}
