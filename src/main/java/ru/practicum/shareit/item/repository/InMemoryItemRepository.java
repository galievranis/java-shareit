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
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(generatedId++);
        items.put(item.getId(), item);
        log.debug("Вещь с ID {} создан.", item.getId());
        return item;
    }

    @Override
    public Item update(Long itemId, ItemDto itemDto) {
        Item oldItem = items.get(itemId);

        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        return items.put(oldItem.getId(), oldItem);
    }

    @Override
    public List<Item> getAll(Long userId) {
        return items.values().stream()
                .filter(i -> i.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> searchItem(String searchCriteria) {
        log.debug("Получен список вещей согласно критерию поиска.");
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase(Locale.ROOT).contains(searchCriteria)
                || i.getDescription().toLowerCase(Locale.ROOT).contains(searchCriteria))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
