package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findAllByUserId(Integer userId);

    List<Item> findAllByText(String text);

    Optional<Item> findById(Integer id);

    Item save(Item item);

    void deleteById(Integer id);

    void deleteByUserId(Integer userId);
}
