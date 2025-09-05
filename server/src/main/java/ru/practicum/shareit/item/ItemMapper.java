package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemShortRequestResponseDto toItemShortRequestResponseDto(Item item) {
        return new ItemShortRequestResponseDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }

    public static ItemShortResponseDto toItemShortResponseDto(Item item) {
        return new ItemShortResponseDto(
                item.getId(),
                item.getName()
        );
    }

    public static ItemResponseDto toItemResponseDto(Item item, Booking lastBooking, Booking nextBooking,
                                                    List<Comment> comments) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner().getId(),
                BookingMapper.toBookingShortResponseDto(lastBooking),
                BookingMapper.toBookingShortResponseDto(nextBooking),
                comments.stream().map(CommentMapper::toCommentResponseDto).toList(),
                item.getRequest() == null ? null : item.getRequest().getId()
        );
    }


    public static Item toItem(ItemCreateDto itemCreateDto, User owner, ItemRequest itemRequest) {
        return new Item(
                null,
                itemCreateDto.name(),
                itemCreateDto.description(),
                itemCreateDto.available(),
                owner,
                itemRequest
        );
    }

    public static Item patchItem(Item item, ItemUpdateDto itemUpdateDto, ItemRequest itemRequest) {
        return new Item(
                item.getId(),
                itemUpdateDto.name() != null ? itemUpdateDto.name() : item.getName(),
                itemUpdateDto.description() != null ? itemUpdateDto.description() : item.getDescription(),
                itemUpdateDto.available() != null ? itemUpdateDto.available() : item.isAvailable(),
                item.getOwner(),
                itemRequest
        );
    }
}
