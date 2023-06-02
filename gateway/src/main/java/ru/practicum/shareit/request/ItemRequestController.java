package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.constants.RequestHeaderConstants.OWNER_ID_HEADER;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST request to add an item request with userId: {}.", userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwn(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET request to get all own requests with userId: {}.", userId);
        return itemRequestClient.getAllOwn(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET request to get all requests with userId: {}.", userId);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {
        log.info("GET request to get an requests by ID: {}.", requestId);
        return itemRequestClient.getById(userId, requestId);
    }
}
