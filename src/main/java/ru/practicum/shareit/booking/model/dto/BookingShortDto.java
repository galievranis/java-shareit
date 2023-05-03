package ru.practicum.shareit.booking.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingShortDto {
    private Long id;
    private Long bookerId;
}
