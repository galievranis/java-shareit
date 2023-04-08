package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Класс базовой модели вещи
 */

@Data
@Builder
@AllArgsConstructor
public class Item {
    private Long id;

    @NotBlank(message = "Название не должно быть пустым.")
    private String name;

    @NotBlank(message = "Описание не должно быть пустым.")
    private String description;

    private Boolean available;
    private Long ownerId;
    private Long request;
}
