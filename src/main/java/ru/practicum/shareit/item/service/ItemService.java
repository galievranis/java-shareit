package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto, Long itemId);

    List<ItemDto> getAll(Long userId);

    ItemDto getById(Long userId, Long itemId);

    List<ItemDto> searchItem(Long userId, String searchCriteria);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
