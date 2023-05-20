package ru.practicum.shareit.request.model.dto;

import lombok.*;
import ru.practicum.shareit.item.model.dto.ItemResponseShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseShortDto> items;
}
