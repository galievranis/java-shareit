package ru.practicum.shareit.item.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @NotBlank(message = "Text can't be empty.")
    private String text;
}
