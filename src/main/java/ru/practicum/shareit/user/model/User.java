package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Класс базовой модели пользователя
 */

@Data
@Builder
@AllArgsConstructor
public class User {
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым.")
    private String name;

    @Email(message = "Email должен быть корректным.")
    @NotBlank(message = "Email не должен быть пустым.")
    private String email;
}
