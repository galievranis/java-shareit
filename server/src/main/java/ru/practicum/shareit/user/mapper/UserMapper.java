package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.entity.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public List<UserDto> toUserDto(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(toUserDto(user));
        }

        return result;
    }
}
