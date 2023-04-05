package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
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
    private boolean available;
    private User owner;
    private User request;
}
