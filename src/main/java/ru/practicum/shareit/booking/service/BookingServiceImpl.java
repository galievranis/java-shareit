package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotCorrectDateException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto create(Long userId, BookingDto bookingDto) {
        User booker = UserMapper.toUser(userService.getById(userId));
        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchElementException(String.format("Item with ID: %d not found.", itemId)));

        validateBookingWhenCreate(bookingDto, item, userId);

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto updateStatus(Long userId, Long bookingId, Boolean approved) {
        userService.getById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchElementException(String.format("Booking with ID: %d not found.", bookingId)));
        Long itemId = booking.getItem().getId();
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchElementException(String.format("Item with ID: %d not found.", itemId)));

        validateBookingWhenUpdate(booking, item, userId);

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getById(Long userId, Long bookingId) {
        userService.getById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchElementException(String.format("Booking with ID: %d not found", bookingId)));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Only the owner or the booker of the item can view the booking.");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getByBookerId(Long userId, String state) {
        userService.getById(userId);
        BookingState bookingState = checkState(state);

        final LocalDateTime now = LocalDateTime.now();

        switch (bookingState) {
            case PAST:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now));
            case FUTURE:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now));
            case CURRENT:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now));
            case WAITING:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED));
            default:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
        }
    }

    @Override
    public List<BookingResponseDto> getByOwnerId(Long userId, String state) {
        userService.getById(userId);
        BookingState bookingState = checkState(state);

        final LocalDateTime now = LocalDateTime.now();

        switch (bookingState) {
            case PAST:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now));
            case FUTURE:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now));
            case CURRENT:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now));
            case WAITING:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED));
            default:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId));
        }
    }

    private BookingState checkState(String state) {
        try {
           return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
    }

    private void validateBookingWhenCreate(BookingDto bookingDto, Item item, Long userId) {
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new PermissionDeniedException("You can't book your own item.");
        }

        if (!item.getAvailable()) {
            throw new NotAvailableException(
                    String.format("Item with ID: %d not available for booking.", item.getId()));
        }

        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new NotCorrectDateException("The booking start time can't be equal to the end time.");
        }
    }

    private void validateBookingWhenUpdate(Booking booking, Item item, Long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new PermissionDeniedException("Only the owner can change the booking status.");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new NotAvailableException(String.format("You can accept or reject " +
                    "a booking only if the booking status is 'Waiting'. " +
                    "Current booking status is: %s.", booking.getStatus()));
        }
    }
}
