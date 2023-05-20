package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.util.markers.Create;
import ru.practicum.shareit.util.markers.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(groups = Create.class, message = "Name can't be empty.")
    private String name;

    @NotEmpty(groups = Create.class, message = "Email can't be empty.")
    @Email(groups = {Create.class, Update.class})
    private String email;
}
