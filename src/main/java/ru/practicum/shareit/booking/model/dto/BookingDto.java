package ru.practicum.shareit.booking.model.dto;

import lombok.*;
import ru.practicum.shareit.booking.validator.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private long id;

    @NotNull
    @FutureOrPresent(message = "The start date of the booking can't be in the past time.")
    @StartBeforeEndDateValid
    private LocalDateTime start;

    @NotNull
    @Future(message = "The end date of the booking can't be in the past time.")
    private LocalDateTime end;

    @NotNull(message = "Item ID can't be empty.")
    private Long itemId;
}
