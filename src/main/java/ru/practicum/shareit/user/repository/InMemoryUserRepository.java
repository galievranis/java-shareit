package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long generatedId = 1L;

    @Override
    public UserDto create(UserDto userDto) {
        userDto.setId(generatedId++);
        users.put(userDto.getId(), UserMapper.toUser(userDto));
        log.debug("Пользователь с ID {} создан.", userDto.getId());
        return userDto;
    }

    @Override
    public User update(Long userId, UserDto userDto) {
        User userToUpdate = users.get(userId);

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            userToUpdate.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            userToUpdate.setEmail(userDto.getEmail());
        }

        log.debug("Пользователь с ID {} обновлен.", userToUpdate.getId());
        return userToUpdate;
    }

    @Override
    public List<User> getAll() {
        log.debug("Получен список всех пользователей.");
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Пользователь с ID {} удален.", id);
        users.remove(id);
    }
}