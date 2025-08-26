package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemResponseDto> getAllItemsByUserId(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        return itemRepository.findAllByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemResponseDto> getAllItemsByText(Integer userId, String text) {
        // For all users
        return text.isBlank() ? List.of() : itemRepository.findAllByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemResponseDto getItemById(Integer userId, Integer itemId) {
        // For all users.
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item not found: id = %d", itemId)));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemResponseDto createItem(Integer userId, ItemCreateDto itemCreateDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        Item createItem = itemRepository.save(ItemMapper.toItem(itemCreateDto, userId));
        log.info("Create item: {}", createItem);
        return ItemMapper.toItemDto(createItem);
    }

    @Override
    public ItemResponseDto updateItem(Integer userId, ItemUpdateDto itemUpdateDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        Item updateItem = itemRepository.findById(itemUpdateDto.id())
                .orElseThrow(() -> new NotFoundException(String.format("Item not found: id = %d", itemUpdateDto.id())));
        if (!userId.equals(updateItem.getOwnerId())) {
            throw new ForbiddenException("Only owners allowed");
        }
        updateItem = ItemMapper.patchItem(updateItem, itemUpdateDto);
        updateItem = itemRepository.save(updateItem);
        log.info("Update item: {}", updateItem);
        return ItemMapper.toItemDto(updateItem);
    }

    @Override
    public void deleteItemById(Integer userId, Integer itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        Item updateItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item not found: id = %d", itemId)));
        if (!userId.equals(updateItem.getOwnerId())) {
            throw new ForbiddenException("Only owners allowed");
        }
        itemRepository.deleteById(itemId);
        log.info("Delete item: id = {}", itemId);
    }
}
