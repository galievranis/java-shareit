package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getById(Long userId, Long id);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getAllOwn(Long userId, Integer from, Integer size);
}
