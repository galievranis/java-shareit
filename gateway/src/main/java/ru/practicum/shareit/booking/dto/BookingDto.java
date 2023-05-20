package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.FutureOrPresent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.validator.start.StartBeforeEndDateValid;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingDto {
	private long itemId;

	@FutureOrPresent(message = "The start date of the booking can't be in the past time.")
	private LocalDateTime start;

	private LocalDateTime end;
}
