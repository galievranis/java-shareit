package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.RequestHeaderConstants.OWNER_ID_HEADER;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto create(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                  @Valid @RequestBody ItemDto itemDto) {
        log.info("POST request to add an item.");
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ItemResponseDto update(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                 @RequestBody ItemDto itemDto,
                                 @PathVariable Long itemId) {
        log.info("PATCH request to update an item with ID {} from a user with ID: {}.", itemId, userId);
        return itemService.update(userId, itemDto, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getAllByUserId(@RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("GET request to get all items by user with ID: {}.", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("{itemId}")
    public ItemResponseDto getById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                  @PathVariable Long itemId) {
        log.info("GET request to get item with ID: {}.", itemId);
        return itemService.getById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                        @RequestParam(value = "text") String searchCriteria) {
        log.info("GET request to get all items by search criteria: {}.", searchCriteria);

        if (searchCriteria.isBlank()) {
            return List.of();
        }

        return itemService.searchItem(userId, searchCriteria);
    }

    @PostMapping("{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody @Valid CommentDto commentDto) {
        log.info("POST request to add comment from user with ID: {}.", userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
