package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {

    public BookingResponseDto toBookingResponseDto(Booking booking) {
        BookingResponseDto.Item item = BookingResponseDto.Item.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();

        BookingResponseDto.Booker booker = BookingResponseDto.Booker.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .build();

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(item)
                .booker(booker)
                .build();
    }

    public Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingShortDto toBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public List<BookingResponseDto> toBookingResponseDto(Iterable<Booking> bookings) {
        List<BookingResponseDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(toBookingResponseDto(booking));
        }

        return result;
    }
}
