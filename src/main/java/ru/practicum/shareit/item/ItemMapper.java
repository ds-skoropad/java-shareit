package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemResponseDto toItemDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public static Item toItem(ItemCreateDto itemCreateDto, Integer userId) {
        return new Item(
                null,
                itemCreateDto.name(),
                itemCreateDto.description(),
                itemCreateDto.available(),
                userId
        );
    }

    public static Item patchItem(Item item, ItemUpdateDto itemUpdateDto) {
        return new Item(
                item.getId(),
                itemUpdateDto.name() != null ? itemUpdateDto.name() : item.getName(),
                itemUpdateDto.description() != null ? itemUpdateDto.description() : item.getDescription(),
                itemUpdateDto.available() != null ? itemUpdateDto.available() : item.isAvailable(),
                item.getOwnerId()
        );
    }
}
