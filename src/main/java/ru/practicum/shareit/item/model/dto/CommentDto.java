package ru.practicum.shareit.item.model.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank(message = "Text can't be empty.")
    private String text;
}
