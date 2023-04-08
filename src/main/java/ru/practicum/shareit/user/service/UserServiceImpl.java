package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(User user) {
        validateUserEmailWhenCreate(user.getEmail());
        return UserMapper.toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        getById(id);
        validateUserEmailWhenUpdate(id, userDto.getEmail());
        return UserMapper.toUserDto(userRepository.update(id, userDto));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        Optional<User> user = userRepository.getById(id);

        if (user.isEmpty()) {
            log.debug("Пользователь с ID {} не найден.", id);
            throw new NoSuchElementException(
                    String.format("Пользователь с ID %d не найден.", id));
        }

        log.debug("Получен пользователь с ID {}.", id);
        return UserMapper.toUserDto(user.get());
    }


    @Override
    public void deleteById(Long id) {
        getById(id);
        userRepository.deleteById(id);
    }

    /**
     * Валидация email пользователя на уникальность при создании пользователя
     * @param email – email, который поступает на вход
     */

    private void validateUserEmailWhenCreate(String email) {
        boolean emailExists = getAll().stream()
                .anyMatch(u -> u.getEmail().equals(email));

        if (emailExists) {
            log.debug("Пользователь с email {} уже существует", email);
            throw new DuplicateEmailException(
                    String.format("Пользователь с email %s уже существует.", email));
        }
    }

    /**
     * Валидация email пользователя на уникальность при обновлении пользователя
     * @param email – email, который поступает на вход
     * @param excludeUserId – ID пользователя, которого нужно исключить из валидации
     */

    private void validateUserEmailWhenUpdate(Long excludeUserId, String email) {
        boolean emailExists = getAll().stream()
                .filter(u -> !u.getId().equals(excludeUserId))
                .anyMatch(u -> u.getEmail().equals(email));

        if (emailExists) {
            log.debug("Пользователь с email {} уже существует", email);
            throw new DuplicateEmailException(
                    String.format("Пользователь с email %s уже существует.", email));
        }
    }
}