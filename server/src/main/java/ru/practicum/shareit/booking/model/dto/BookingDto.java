package ru.practicum.shareit.booking.model.dto;

import lombok.*;
import ru.practicum.shareit.util.start.StartBeforeEndDateValid;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
