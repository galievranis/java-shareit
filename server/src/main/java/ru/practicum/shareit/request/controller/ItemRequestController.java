package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.util.constants.RequestHeaderConstants.OWNER_ID_HEADER;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST request to add an item request with userId: {}.", userId);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllOwn(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET request to get all own requests with userId: {}.", userId);
        return itemRequestService.getAllOwn(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET request to get all requests with userId: {}.", userId);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getById(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {
        log.info("GET request to get an requests by ID: {}.", requestId);
        return itemRequestService.getById(userId, requestId);
    }
}
