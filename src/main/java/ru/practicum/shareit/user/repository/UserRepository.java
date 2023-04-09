package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    UserDto create(UserDto userDto);

    User update(Long userId, UserDto userDto);

    List<User> getAll();

    Optional<User> getById(Long id);

    void deleteById(Long id);
}