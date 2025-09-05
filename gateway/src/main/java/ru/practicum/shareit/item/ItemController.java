package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static ru.practicum.shareit.ShareItConstants.REQ_HEAD_USER_ID;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId) {
        return itemClient.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @RequestParam(defaultValue = "") String text) {
        return itemClient.getItemsByText(userId, text);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @PathVariable @Min(1) Integer itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @Valid @RequestBody ItemCreateDto itemCreateDto) {
        return itemClient.createItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @PathVariable @Min(1) Integer itemId,
            @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemClient.updateItem(userId, itemId, itemUpdateDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @PathVariable @Min(1) Integer itemId,
            @Valid @RequestBody CommentCreateDto commentCreateDto) {
        return itemClient.createComment(userId, itemId, commentCreateDto);
    }
}
