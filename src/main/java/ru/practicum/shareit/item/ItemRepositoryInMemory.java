package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, List<Item>> itemsByUserId = new HashMap<>();
    private int nextId = 1;

    @Override
    public List<Item> findAllByUserId(Integer userId) {
        return itemsByUserId.get(userId);
    }

    @Override
    public List<Item> findAllByText(String text) {
        return items.values().stream()
                .filter(item ->
                        item.isAvailable() && (item.getName().toUpperCase().contains(text)
                                || item.getDescription().toUpperCase().contains(text))
                )
                .toList();
    }

    @Override
    public Optional<Item> findById(Integer id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.put(item.getId(), item);
        List<Item> userItems = itemsByUserId.containsKey(item.getOwnerId()) ?
                itemsByUserId.get(item.getOwnerId()) : new ArrayList<>();
        userItems.add(item);
        itemsByUserId.put(item.getOwnerId(), userItems);
        return item;
    }

    @Override
    public void deleteById(Integer id) {
        items.remove(id);
    }

    @Override
    public void deleteByUserId(Integer userId) {
        if (itemsByUserId.containsKey(userId)) {
            itemsByUserId.get(userId).forEach(item -> items.remove(item.getId()));
            itemsByUserId.remove(userId);
        }
    }
}
