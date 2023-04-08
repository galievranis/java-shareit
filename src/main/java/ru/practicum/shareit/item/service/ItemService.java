package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);
    ItemDto update(Long userId, ItemDto itemDto, Long itemId);
    List<ItemDto> getAll(Long userId);
    ItemDto getById(Long userId, Long itemID);
    List<ItemDto> searchItem(Long userId, String searchCriteria);
}
