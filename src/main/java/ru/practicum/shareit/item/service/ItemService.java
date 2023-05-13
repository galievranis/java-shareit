package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ItemResponseDto create(Long userId, ItemDto itemDto);

    ItemResponseDto update(Long userId, ItemDto itemDto, Long itemId);

    List<ItemResponseDto> getAll(Long userId, Integer from, Integer size);

    ItemResponseDto getById(Long userId, Long itemId);

    List<ItemResponseDto> searchItem(Long userId, String searchCriteria, Integer from, Integer size);

    CommentResponseDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
