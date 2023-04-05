package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long generatedId = 1L;

    @Override
    public User create(User user) {
        user.setId(generatedId++);
        users.put(user.getId(), user);
        log.debug("Пользователь с ID {} создан.", user.getId());
        return user;
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User userToUpdate = users.get(userId);

        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            userToUpdate.setEmail(userDto.getEmail());
        }

        users.put(userId, userToUpdate);
        log.debug("Пользователь с ID {} обновлен.", userToUpdate.getId());
        return UserMapper.toUserDto(userToUpdate);
    }

    @Override
    public List<User> getAll() {
        log.debug("Получен список всех пользователей.");
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        log.debug("Получен пользователь с ID {}.", id);
        return Optional.of(users.get(id));
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Пользователь с ID {} удален.", id);
        users.remove(id);
    }
}