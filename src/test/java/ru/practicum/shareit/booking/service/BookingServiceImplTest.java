package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("BookingService tests")
@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("'create' should create booking successfully")
    public void createBooking_Success() {
        User booker = createUser1();
        User user = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);
        BookingDto bookingDto = createBookingDto(booking);
        BookingResponseDto expectedBooking = BookingMapper.toBookingResponseDto(booking);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingResponseDto actualBooking = bookingService.create(booker.getId(), bookingDto);

        assertNotNull(actualBooking);
        assertThat(actualBooking.getId(), equalTo(expectedBooking.getId()));
        assertThat(actualBooking.getStart(), equalTo(expectedBooking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(expectedBooking.getEnd()));
        assertThat(actualBooking.getStatus(), equalTo(expectedBooking.getStatus()));
        assertThat(actualBooking.getBooker().getId(), equalTo(expectedBooking.getBooker().getId()));
        assertThat(actualBooking.getItem().getId(), equalTo(expectedBooking.getItem().getId()));
    }

    @Test
    @DisplayName("'create' should throw exception when user not found")
    public void createBooking_UserNotFound() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);
        BookingDto bookingDto = createBookingDto(booking);
        Long id = 2L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                bookingService.create(id, bookingDto));

        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'create' should throw exception when item not found")
    public void createBooking_ItemNotFound() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);
        BookingDto bookingDto = createBookingDto(booking);
        Long id = 1L;

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                bookingService.create(user.getId(), bookingDto));

        assertEquals(String.format("Item with ID: %d not found.", id), exception.getMessage());
        verify(itemRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'create' should throw exception when owner try to book his own item")
    public void createBooking_OwnerCanNotBookHisOwnItem() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);
        BookingDto bookingDto = createBookingDto(booking);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () ->
                bookingService.create(user.getId(), bookingDto));

        assertEquals("You can't book your own item.", exception.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    @DisplayName("'create' should throw exception when item not available for booking")
    public void createBooking_ItemNotAvailable() {
        User booker = createUser1();
        User user = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        item.setAvailable(false);
        Booking booking = createBooking1(user, item);
        BookingDto bookingDto = createBookingDto(booking);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        NotAvailableException exception = assertThrows(NotAvailableException.class, () ->
                bookingService.create(booker.getId(), bookingDto));

        assertEquals(String.format("Item with ID: %d not available for booking.", item.getId()), exception.getMessage());
        verify(userRepository, times(1)).findById(booker.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    @DisplayName("'update' should update booking status to 'APPROVED")
    public void updateBookingStatusToApprove_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingResponseDto actualBooking = bookingService.updateStatus(user.getId(), booking.getId(), true);

        assertNotNull(actualBooking);
        assertThat(actualBooking.getStatus(), equalTo(BookingStatus.APPROVED));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    @DisplayName("'update' should update booking status to 'REJECTED")
    public void updateBookingStatusToRejected_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingResponseDto actualBooking = bookingService.updateStatus(user.getId(), booking.getId(), false);

        assertNotNull(actualBooking);
        assertThat(actualBooking.getStatus(), equalTo(BookingStatus.REJECTED));
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    @DisplayName("'update' should throw exception when user not found")
    public void updateBookingStatus_UserNotFound() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);
        Long id = 2L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                bookingService.updateStatus(id, booking.getId(), true));

        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'update' should throw exception when booking not found")
    public void updateBookingStatus_BookingNotFound() {
        User user = createUser1();
        Long id = 1L;

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                bookingService.updateStatus(user.getId(), id, true));

        assertEquals(String.format("Booking with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'update' should throw exception when item not found")
    public void updateBookingStatus_ItemNotFound() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);
        Long id = 1L;

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                bookingService.updateStatus(user.getId(), booking.getId(), true));

        assertEquals(String.format("Item with ID: %d not found.", id), exception.getMessage());
        verify(itemRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findById(booking.getId());
        verify(itemRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'update' should throw exception when user not owner of the booking")
    public void updateBookingStatus_UserNotOwner() {
        User owner = createUser1();
        User notOwner = createUser2();
        ItemRequest itemRequest = createItemRequest(owner);
        Item item = createItem(owner, itemRequest);
        Booking booking = createBooking1(owner, item);

        when(userRepository.findById(notOwner.getId()))
                .thenReturn(Optional.of(notOwner));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () ->
                bookingService.updateStatus(notOwner.getId(), booking.getId(), true));

        assertEquals("Only the owner can change the booking status.", exception.getMessage());
    }

    @Test
    @DisplayName("'update' should throw exception when booking status isn't 'WAITING'")
    public void updateBookingStatus_StatusIsNotWaiting() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);
        booking.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        NotAvailableException exception = assertThrows(NotAvailableException.class, () ->
                bookingService.updateStatus(user.getId(), booking.getId(), true));

        assertEquals(String.format("You can accept or reject a booking only " +
                "if the booking status is 'Waiting'. Current booking status is: %s.",
                booking.getStatus()), exception.getMessage());
    }

    @Test
    @DisplayName("'getById' should return booking by booking ID successfully")
    public void getBookingByBookingId_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);
        BookingResponseDto expectedBooking = BookingMapper.toBookingResponseDto(booking);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingResponseDto actualBooking = bookingService.getById(user.getId(), 1L);

        assertNotNull(actualBooking);
        assertThat(actualBooking.getId(), equalTo(expectedBooking.getId()));
        assertThat(actualBooking.getItem().getId(), equalTo(expectedBooking.getItem().getId()));
        assertThat(actualBooking.getStart(), equalTo(expectedBooking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(expectedBooking.getEnd()));
        assertThat(actualBooking.getStatus(), equalTo(expectedBooking.getStatus()));
        assertThat(actualBooking.getBooker().getId(), equalTo(expectedBooking.getBooker().getId()));
    }

    @Test
    @DisplayName("'getById' should throw exception when user not found")
    public void getBookingByBookingId_UserNotFound() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);
        Long id = 2L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                bookingService.getById(id, booking.getId()));

        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'getById' should throw exception when user isn't owner or booker")
    public void getBookingByBookingId_UserNotOwnerOrBooker() {
        User user = createUser1();
        User notOwner = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(user, item);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(notOwner));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));


        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () ->
                bookingService.getById(notOwner.getId(), booking.getId()));

        assertEquals("Only the owner or the booker of the item can view the booking.", exception.getMessage());
    }

    @Test
    @DisplayName("'getByBookerId' should return all 'PAST' bookings by booker ID")
    public void getPastBookingsByBookerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> pastBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(pastBookings);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pastBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByBookerId(booker.getId(), "PAST", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByBookerId' should return all 'FUTURE' bookings by booker ID")
    public void getFutureBookingsByBookerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> futureBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(futureBookings);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(futureBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByBookerId(booker.getId(), "FUTURE", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByBookerId' should return all 'CURRENT' bookings by booker ID")
    public void getCurrentBookingsByBookerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> currentBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(currentBookings);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(currentBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByBookerId(booker.getId(), "CURRENT", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByBookerId' should return all 'WAITING' bookings by booker ID")
    public void getWaitingBookingsByBookerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> waitingBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(waitingBookings);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(waitingBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByBookerId(booker.getId(), "WAITING", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByBookerId' should return all 'REJECTED' bookings by booker ID")
    public void getRejectedBookingsByBookerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> rejectedBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(rejectedBookings);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(rejectedBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByBookerId(booker.getId(), "REJECTED", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByBookerId' should return all bookings by booker ID")
    public void getAllBookingsByBookerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> allBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(allBookings);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(allBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByBookerId(booker.getId(), "ALL", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));

    }

    @Test
    @DisplayName("'getByBookerId' should throw exception when user not found")
    public void getAllBookingsByBookerId_UserNotFound() {
        Long id = 2L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                bookingService.getByBookerId(id, "ALL", 0, 10));

        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'getByBookerId' should throw exception when state is invalid")
    public void getAllBookingsByBookerId_InvalidState() {
        User booker = createUser1();
        String state = "Unknown State";

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.getByBookerId(booker.getId(), state, 0, 10));

        assertEquals(String.format("Unknown state: %s", state), exception.getMessage());
        verify(userRepository, times(1)).findById(booker.getId());
    }

    @Test
    @DisplayName("'getByOwnerId' should return all 'PAST' bookings by owner ID")
    public void getPastBookingsByOwnerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> pastBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(pastBookings);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(pastBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByOwnerId(user.getId(), "PAST", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByOwnerId' should return all 'FUTURE' bookings by owner ID")
    public void getFutureBookingsByOwnerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> futureBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(futureBookings);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(futureBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByOwnerId(user.getId(), "FUTURE", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByOwnerId' should return all 'CURRENT' bookings by owner ID")
    public void getCurrentBookingsByOwnerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> currentBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(currentBookings);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(currentBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByOwnerId(user.getId(), "CURRENT", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByOwnerId' should return all 'WAITING' bookings by owner ID")
    public void getWaitingBookingsByOwnerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> waitingBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(waitingBookings);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(waitingBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByOwnerId(user.getId(), "WAITING", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByOwnerId' should return all 'REJECTED' bookings by owner ID")
    public void getRejectedBookingsByOwnerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> rejectedBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(rejectedBookings);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(rejectedBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByOwnerId(user.getId(), "REJECTED", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByOwnerId' should return all bookings by owner ID")
    public void getAllBookingsByOwnerId_Success() {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking1(booker, item);
        List<Booking> allBookings = List.of(booking);
        List<BookingResponseDto> expectedBookings = BookingMapper.toBookingResponseDto(allBookings);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(allBookings);

        List<BookingResponseDto> actualBookings = bookingService.getByOwnerId(user.getId(), "ALL", 0, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.get(0).getId(), equalTo(expectedBookings.get(0).getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(expectedBookings.get(0).getItem().getId()));
        assertThat(actualBookings.get(0).getStart(), equalTo(expectedBookings.get(0).getStart()));
        assertThat(actualBookings.get(0).getEnd(), equalTo(expectedBookings.get(0).getEnd()));
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(expectedBookings.get(0).getBooker().getId()));
    }

    @Test
    @DisplayName("'getByOwnerId' should throw exception when user not found")
    public void getBookingsByOwnerId_UserNotFound() {
        Long id = 2L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                bookingService.getByOwnerId(id, "ALL", 0, 10));

        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'getByOwnerId' should throw exception when state is invalid")
    public void getAllBookingsByOwnerId_InvalidState() {
        User owner = createUser1();
        String state = "Unknown State";

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.getByOwnerId(owner.getId(), state, 0, 10));

        assertEquals(String.format("Unknown state: %s", state), exception.getMessage());
        verify(userRepository, times(1)).findById(owner.getId());
    }

    private User createUser1() {
        return User.builder()
                .id(1L)
                .name("User 1")
                .email("user1Email@mail.com")
                .build();
    }

    private User createUser2() {
        return User.builder()
                .id(2L)
                .name("User 2")
                .email("user2Email@mail.com")
                .build();
    }

    private ItemRequest createItemRequest(User requestor) {
        return ItemRequest.builder()
                .id(1L)
                .description("Item request 1 description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    private Item createItem(User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(1L)
                .name("Item 1 name")
                .description("Item 1 description")
                .owner(owner)
                .available(true)
                .request(itemRequest)
                .build();
    }

    private Booking createBooking1(User user, Item item) {
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.WAITING)
                .booker(user)
                .item(item)
                .build();
    }

    private BookingDto createBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .build();
    }
}
