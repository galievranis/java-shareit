package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        userService.getById(userId);
        User owner = UserMapper.toUser(userService.getById(userId));
        Item item = itemRepository.create(ItemMapper.toItem(itemDto, owner));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        Item oldItem = itemRepository.getById(itemId).orElseThrow(() ->
                new NoSuchElementException(
                    String.format("Вещь с ID %d не найдена.", itemId)));

        if (!oldItem.getOwnerId().equals(userId)) {
            throw new PermissionDeniedException(
                    String.format("Пользователь с ID %d не может редактировать этот файл.", userId));
        }

        return ItemMapper.toItemDto(itemRepository.update(oldItem, itemDto));
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        userService.getById(userId);

        return itemRepository.getAll(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        Item item = itemRepository.getById(itemId).orElseThrow(() ->
                new NoSuchElementException(
                        String.format("Вещь с ID %d не найдена.", itemId)));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String searchCriteria) {
        String searchCriteriaInLowerCase = searchCriteria.toLowerCase();

        return itemRepository.searchItem(searchCriteriaInLowerCase).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
