package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(userIdHeader) Long userId,
                              @RequestBody @Valid ItemDto itemDto) {
        log.info("POST запрос на добавление вещи.");
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader(userIdHeader) Long userId,
                              @RequestBody @Valid ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.info("PATCH запрос на обновление вещи с ID {} от пользователя с ID {}.", itemId, userId);
        return itemService.update(userId, itemDto, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(userIdHeader) Long userId) {
        log.info("GET запрос на получение списка вещей пользователя с ID {}.", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@RequestHeader(userIdHeader) Long userId,
                               @PathVariable Long itemId) {
        log.info("GET запрос на получение вещи с ID {}.", itemId);
        return itemService.getById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(userIdHeader) Long userId,
                                    @RequestParam(value = "text") String searchCriteria) {
        log.info("GET запрос на поиск вещей по критерию.");
        return itemService.searchItem(userId, searchCriteria);
    }
}
