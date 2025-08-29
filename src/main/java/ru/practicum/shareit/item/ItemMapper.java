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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

    public static List<ItemResponseDto> toListItemResponseDto(List<Item> items, Map<Integer, List<Comment>> commentsMap,
                                                              Map<Integer, List<Booking>> bookingMap) {
        return items.stream()
                .map(item -> {
                    Integer itemId = item.getId();
                    Booking lastBooking = null;
                    Booking nextBooking = null;

                    if (bookingMap != null) {
                        LocalDateTime now = LocalDateTime.now();
                        List<Booking> bookings = bookingMap.getOrDefault(itemId, List.of());
                        lastBooking = bookings.stream()
                                .filter(b -> b.getEnd().isBefore(now))
                                .max(Comparator.comparing(Booking::getEnd))
                                .orElse(null);
                        nextBooking = bookings.stream()
                                .filter(b -> b.getStart().isAfter(now))
                                .min(Comparator.comparing(Booking::getStart))
                                .orElse(null);
                    }

                    return toItemResponseDto(
                            item,
                            lastBooking,
                            nextBooking,
                            commentsMap == null ? List.of() : commentsMap.getOrDefault(itemId, List.of())
                    );
                })
                .toList();
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
