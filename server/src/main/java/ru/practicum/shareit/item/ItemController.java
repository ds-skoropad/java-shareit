package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

import static ru.practicum.shareit.ShareItConstants.REQ_HEAD_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> getItemsByUserId(
            @RequestHeader(REQ_HEAD_USER_ID) Integer userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> getItemsByText(
            @RequestHeader(REQ_HEAD_USER_ID) Integer userId,
            @RequestParam(defaultValue = "") String text) {
        return itemService.getItemsByText(userId, text);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(
            @RequestHeader(REQ_HEAD_USER_ID) Integer userId,
            @PathVariable Integer itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto createItem(
            @RequestHeader(REQ_HEAD_USER_ID) Integer userId,
            @RequestBody ItemCreateDto itemCreateDto) {
        return itemService.createItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(
            @RequestHeader(REQ_HEAD_USER_ID) Integer userId,
            @PathVariable Integer itemId,
            @RequestBody ItemUpdateDto itemUpdateDto) {
        itemUpdateDto = new ItemUpdateDto(
                itemId,
                itemUpdateDto.name(),
                itemUpdateDto.description(),
                itemUpdateDto.available(),
                itemUpdateDto.requestId());
        return itemService.updateItem(userId, itemUpdateDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @RequestHeader(REQ_HEAD_USER_ID) Integer userId,
            @PathVariable Integer itemId,
            @RequestBody CommentCreateDto commentCreateDto) {
        return itemService.createComment(userId, itemId, commentCreateDto);
    }
}
