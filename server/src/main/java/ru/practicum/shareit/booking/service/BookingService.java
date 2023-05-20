package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(Long userId, BookingDto bookingDto);

    BookingResponseDto updateStatus(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto getById(Long userId, Long bookingId);

    List<BookingResponseDto> getByBookerId(Long userId, String state, Integer from, Integer size);

    List<BookingResponseDto> getByOwnerId(Long userId, String state, Integer from, Integer size);
}
