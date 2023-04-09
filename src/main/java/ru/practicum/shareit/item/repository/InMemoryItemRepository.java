package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private Long generatedId = 1L;
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(generatedId++);
        final List<Item> items = userItemIndex.computeIfAbsent(
                item.getOwnerId(), k -> new ArrayList<>());
        items.add(item);
        userItemIndex.put(item.getOwnerId(), items);

        log.debug("Вещь с ID {} создан.", item.getId());
        return item;
    }

    @Override
    public Item update(Item oldItem, ItemDto newItem) {

        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            oldItem.setName(newItem.getName());
        }

        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
            oldItem.setDescription(newItem.getDescription());
        }

        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        return oldItem;
    }

    @Override
    public List<Item> getAll(Long userId) {
        return userItemIndex.get(userId);
    }

    @Override
    public Optional<Item> getById(Long itemId) {
        return userItemIndex.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public List<Item> searchItem(String searchCriteria) {
        log.debug("Получен список вещей согласно критерию поиска.");
        return userItemIndex.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(searchCriteria)
                || item.getDescription().toLowerCase(Locale.ROOT).contains(searchCriteria))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
