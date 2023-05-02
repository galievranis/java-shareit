package ru.practicum.shareit.item.model.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.dto.BookingShortDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentResponseDto> comments;
}
