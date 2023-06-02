package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;

import static ru.practicum.shareit.util.constants.RequestHeaderConstants.OWNER_ID_HEADER;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto create(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @RequestBody ItemDto itemDto) {
        log.info("POST request to add an item with userId: {}.", userId);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ItemResponseDto update(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @RequestBody ItemDto itemDto,
            @PathVariable Long itemId) {
        log.info("PATCH request to update an item with ID {} from a user with ID: {}.", itemId, userId);
        return itemService.update(userId, itemDto, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getAllByUserId(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET request to get all items by user with ID: {}.", userId);
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("{itemId}")
    public ItemResponseDto getById(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PathVariable Long itemId) {
        log.info("GET request to get item with ID: {}.", itemId);
        return itemService.getById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @RequestParam(value = "text") String searchCriteria,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET request to get all items by search criteria: {}.", searchCriteria);
        return itemService.searchItem(userId, searchCriteria, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentResponseDto addComment(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        log.info("POST request to add comment from user with ID: {}.", userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
