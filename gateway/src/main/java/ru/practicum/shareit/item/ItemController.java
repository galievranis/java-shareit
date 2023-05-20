package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;

import static ru.practicum.shareit.util.constants.RequestHeaderConstants.OWNER_ID_HEADER;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        log.info("POST request to add an item with userId: {}.", userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @RequestBody ItemDto itemDto,
            @PathVariable Long itemId) {
        log.info("PATCH request to update an item with ID {} from a user with ID: {}.", itemId, userId);
        return itemClient.update(userId, itemDto, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET request to get all items by user with ID: {}.", userId);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PathVariable Long itemId) {
        log.info("GET request to get item with ID: {}.", itemId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @RequestParam(value = "text") String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET request to get all items by search criteria: {}.", text);

        if (text.isBlank()) {
            return ResponseEntity.status(HttpStatus.OK).body(List.of());
        }

        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("POST request to add comment from user with ID: {}.", userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
