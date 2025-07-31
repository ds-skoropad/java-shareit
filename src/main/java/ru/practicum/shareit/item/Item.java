package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    Integer id;
    String name;
    String description;
    boolean available;
    Integer ownerId;
}
