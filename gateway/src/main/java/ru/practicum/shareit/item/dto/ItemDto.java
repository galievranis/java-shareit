package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    @NotBlank(message = "Name can't be empty.")
    private String name;

    @NotBlank(message = "Description can't be empty.")
    private String description;

    @NotNull(message = "Available can't be null.")
    private Boolean available;

    private Long requestId;
}
