package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class User {
    @NotBlank
    private long id;

    private String name;

    @Email
    @NotBlank
    private String email;
}
