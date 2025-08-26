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
        return itemsByUserId.getOrDefault(userId, List.of()).stream()
                .map(this::getItemClone)
                .toList();
    }

    @Override
    public List<Item> findAllByText(String text) {
        return items.values().stream()
                .filter(item ->
                        item.isAvailable() && (item.getName().toUpperCase().contains(text)
                                || item.getDescription().toUpperCase().contains(text))
                )
                .map(this::getItemClone)
                .toList();
    }

    @Override
    public Optional<Item> findById(Integer id) {
        Item item = items.get(id);
        return Optional.ofNullable(item == null ? null : getItemClone(item));
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
        }
        items.put(item.getId(), item);
        itemsByUserId.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>()).add(item);
        return getItemClone(item);
    }

    @Override
    public void deleteById(Integer id) {
        if (items.containsKey(id)) {
            itemsByUserId.remove(items.get(id).getOwnerId());
            items.remove(id);
        }
    }

    @Override
    public void deleteByUserId(Integer userId) {
        if (itemsByUserId.containsKey(userId)) {
            itemsByUserId.get(userId).forEach(item -> items.remove(item.getId()));
            itemsByUserId.remove(userId);
        }
    }

    private Item getItemClone(Item item) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwnerId()
        );
    }
}
