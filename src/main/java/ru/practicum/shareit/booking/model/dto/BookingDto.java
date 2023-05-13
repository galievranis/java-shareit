package ru.practicum.shareit.booking.model.dto;

import lombok.*;
import ru.practicum.shareit.util.validator.start.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingDto {
    private long id;

    @FutureOrPresent(message = "The start date of the booking can't be in the past time.")
    private LocalDateTime start;
    private LocalDateTime end;

    @NotNull(message = "Item ID can't be empty.")
    private Long itemId;
}
