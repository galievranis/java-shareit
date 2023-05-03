package ru.practicum.shareit.booking.model.dto;

import lombok.*;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Item item;
    private Booker booker;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Booker {
        private Long id;
        private String name;
    }
}
