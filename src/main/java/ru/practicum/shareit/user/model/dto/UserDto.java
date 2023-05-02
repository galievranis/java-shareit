package ru.practicum.shareit.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(groups = Create.class, message = "Name can't be empty.")
    private String name;

    @NotEmpty(groups = Create.class, message = "Email can't be empty.")
    @Email(groups = {Create.class, Update.class})
    private String email;
}
