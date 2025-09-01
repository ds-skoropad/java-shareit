package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

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
                comments.stream().map(CommentMapper::toCommentResponseDto).toList()
        );
    }


    public static Item toItem(ItemCreateDto itemCreateDto, User owner) {
        return new Item(
                null,
                itemCreateDto.name(),
                itemCreateDto.description(),
                itemCreateDto.available(),
                owner
        );
    }

    public static Item patchItem(Item item, ItemUpdateDto itemUpdateDto) {
        return new Item(
                item.getId(),
                itemUpdateDto.name() != null ? itemUpdateDto.name() : item.getName(),
                itemUpdateDto.description() != null ? itemUpdateDto.description() : item.getDescription(),
                itemUpdateDto.available() != null ? itemUpdateDto.available() : item.isAvailable(),
                item.getOwner()
        );
    }
}
