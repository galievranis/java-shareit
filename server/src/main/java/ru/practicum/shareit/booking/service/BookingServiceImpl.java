package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.pagination.OffsetPageRequest;
import ru.practicum.shareit.user.model.entity.User;
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
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto create(Long userId, BookingDto bookingDto) {
        User booker = getUserById(userId);
        Long itemId = bookingDto.getItemId();
        Item item = getItemById(itemId);

        validateBookingWhenCreate(item, userId);

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto updateStatus(Long userId, Long bookingId, Boolean approved) {
        getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchElementException(String.format("Booking with ID: %d not found.", bookingId)));
        Item item = getItemById(booking.getItem().getId());

        validateBookingWhenUpdate(booking, item, userId);

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getById(Long userId, Long bookingId) {
        getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchElementException(String.format("Booking with ID: %d not found", bookingId)));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Only the owner or the booker of the item can view the booking.");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getByBookerId(Long userId, String state, Integer from, Integer size) {
        getUserById(userId);
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown state: %s", state)));
        Pageable pageable = PageRequest.of(from / size, size);
        final LocalDateTime now = LocalDateTime.now();

        switch (bookingState) {
            case PAST:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageable));
            case FUTURE:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageable));
            case CURRENT:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, pageable));
            case WAITING:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable));
            default:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable));
        }
    }

    @Override
    public List<BookingResponseDto> getByOwnerId(Long userId, String state, Integer from, Integer size) {
        getUserById(userId);
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown state: %s", state)));
        Pageable pageable = OffsetPageRequest.of(from, size);
        final LocalDateTime now = LocalDateTime.now();

        switch (bookingState) {
            case PAST:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now, pageable));
            case FUTURE:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now, pageable));
            case CURRENT:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, pageable));
            case WAITING:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable));
            default:
                return BookingMapper.toBookingResponseDto(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageable));
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("User with ID: %d not found.", id)));
    }

    private Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("Item with ID: %d not found.", id)));
    }

    private void validateBookingWhenCreate(Item item, Long userId) {
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new PermissionDeniedException("You can't book your own item.");
        }

        if (!item.getAvailable()) {
            throw new NotAvailableException(
                    String.format("Item with ID: %d not available for booking.", item.getId()));
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
