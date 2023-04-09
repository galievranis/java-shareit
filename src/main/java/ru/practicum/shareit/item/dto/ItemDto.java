package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * DTO-класс вещи
 */

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Имя название не должно быть пустым.")
    private String name;

    @NotBlank(message = "Описание не должно быть пустым.")
    private String description;

    @NotNull(message = "Доступность не должна быть пустой.")
    private Boolean available;
    private Long request;
}
