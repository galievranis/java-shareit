package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item create(Item item);

    Item update(Long itemId, ItemDto itemDto);

    List<Item> getAll(Long userId);

    Optional<Item> getById(Long itemId);

    List<Item> searchItem(String searchCriteria);
}
