package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.constants.RequestHeaderConstants.OWNER_ID_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> create(
			@RequestHeader(OWNER_ID_HEADER) Long userId,
			@Valid @RequestBody BookingDto bookingDto) {
		log.info("POST request to add a booking with userId: {}.", userId);
		return bookingClient.create(userId, bookingDto);
	}

	@PatchMapping("{bookingId}")
	public ResponseEntity<Object> update(
			@RequestHeader(OWNER_ID_HEADER) Long userId,
			@PathVariable(value = "bookingId") Long bookingId,
			@RequestParam(value = "approved") Boolean approved) {
		log.info("PATCH request for updating booking status with userId: {}.", userId);
		return bookingClient.updateStatus(userId, bookingId, approved);
	}

	@GetMapping
	public ResponseEntity<Object> getByBookerId(
			@RequestHeader(OWNER_ID_HEADER) Long userId,
			@RequestParam(value = "state", defaultValue = "ALL") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Unknown state: %s", stateParam)));
		log.info("GET request to get all bookings by booker with ID: {}.", userId);
		return bookingClient.getByBookerId(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getByOwnerId(
			@RequestHeader(OWNER_ID_HEADER) Long userId,
			@RequestParam(value = "state", defaultValue = "ALL") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Unknown state: %s", stateParam)));
		log.info("GET request to get all bookings by owner with ID: {}.", userId);
		return bookingClient.getByOwnerId(userId, state, from, size);
	}

	@GetMapping("{bookingId}")
	public ResponseEntity<Object> getById(
			@RequestHeader(OWNER_ID_HEADER) Long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getById(userId, bookingId);
	}
}
