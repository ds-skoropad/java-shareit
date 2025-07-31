package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    private Integer nextId = 1;


    @Override
    public List<Item> findAllByUserId(Integer userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .toList();
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
            item.setId(nextId);
            items.put(nextId, item);
            nextId++;
        } else {
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public void deleteById(Integer id) {
        items.remove(id);
    }

    @Override
    public void deleteByUserId(Integer userId) {
        items.values().removeIf(item -> item.getOwnerId().equals(userId));
    }
}
