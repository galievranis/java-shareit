package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.user.model.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.constants.RequestHeaderConstants.OWNER_ID_HEADER;

@DisplayName("BookingController tests")
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("'create' should create booking successfully'")
    public void createBooking_Success() throws Exception {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking(user, item);
        BookingDto bookingDto = createBookingDto(booking);
        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking);

        when(bookingService.create(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .header(OWNER_ID_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));
    }

    @Test
    @DisplayName("'update' should create booking successfully'")
    public void updateBooking_Success() throws Exception {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking(user, item);
        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking);

        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header(OWNER_ID_HEADER, user.getId())
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));
    }

    @Test
    @DisplayName("'getByBookerId' should return all bookings by booker ID successfully'")
    public void getByBookerId_Success() throws Exception {
        User user = createUser1();
        User booker = createUser2();
        ItemRequest itemRequest = createItemRequest(booker);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking(user, item);
        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking);
        List<BookingResponseDto> bookings = List.of(bookingResponseDto);

        when(bookingService.getByBookerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header(OWNER_ID_HEADER, booker.getId())
                        .content(objectMapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())));
    }

    @Test
    @DisplayName("'getByOwnerId' should return all bookings by owner ID successfully'")
    public void getByOwnerId_Success() throws Exception {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest(user);
        Item item = createItem(user, itemRequest);
        Booking booking = createBooking(user, item);
        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking);
        List<BookingResponseDto> bookings = List.of(bookingResponseDto);

        when(bookingService.getByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header(OWNER_ID_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())));
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

    private Booking createBooking(User user, Item item) {
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(20))
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
