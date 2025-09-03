package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

import static ru.practicum.shareit.ShareItConstants.REQ_HEAD_USER_ID;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createItemRequest(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestService.createItemRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getItemRequestsByUserId(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId) {
        return itemRequestService.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getItemRequests(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId) {
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequestById(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @PathVariable @Min(1) Integer requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
