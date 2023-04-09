package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * DTO-класс пользователя
 */

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(groups = Create.class, message = "Имя не должно быть пустым.")
    private String name;

    @NotEmpty(groups = Create.class, message = "Email не должен быть пустым.")
    @Email(groups = {Create.class, Update.class})
    private String email;
}
