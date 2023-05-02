package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.RequestHeaderConstants.OWNER_ID_HEADER;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                     @RequestBody @Valid BookingDto bookingDto) {
        log.info("POST request to add a booking.");
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingResponseDto update(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                     @PathVariable(value = "bookingId") Long bookingId,
                                     @RequestParam(value = "approved") Boolean approved) {
        log.info("PATCH request for updating booking status.");
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingResponseDto> getAll(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                           @RequestParam(required = false, value = "state", defaultValue = "ALL") String state) {
        log.info("GET request to get all bookings by booker with ID: {}.", userId);
        return bookingService.getByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByOwnerId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                 @RequestParam(required = false, value = "state", defaultValue = "ALL") String state) {
        log.info("GET request to get all bookings by owner with ID: {}.", userId);
        return bookingService.getByOwnerId(userId, state);
    }

    @GetMapping("{bookingId}")
    public BookingResponseDto getById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                      @PathVariable Long bookingId) {
        log.info("GET request to get booking with ID: {}.", bookingId);
        return bookingService.getById(userId, bookingId);
    }
 }
