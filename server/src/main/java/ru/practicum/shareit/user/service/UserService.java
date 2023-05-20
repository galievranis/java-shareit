package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.dto.UserDto;
import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    List<UserDto> getAll();

    UserDto getById(Long id);

    void deleteById(Long id);
}