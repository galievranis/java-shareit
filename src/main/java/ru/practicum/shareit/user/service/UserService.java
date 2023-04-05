package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(User user);
    UserDto update(Long id, UserDto userDto);
    List<UserDto> getAll();
    UserDto getById(Long id);
    void deleteById(Long id);
}