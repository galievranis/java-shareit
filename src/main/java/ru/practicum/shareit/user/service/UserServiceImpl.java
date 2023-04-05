package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ObjectNotFound;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
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
        return userRepository.update(id, userDto);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.toUserDto(userRepository.getById(id)
                .orElseThrow(() -> new ObjectNotFound(String.format("Пользователь с ID %d " +
                        "не найден", id))));
    }

    @Override
    public void deleteById(Long id) {
        getById(id);
        userRepository.deleteById(id);
    }

    private void validateUserEmailWhenCreate(String email) {
        boolean emailExists = getAll().stream()
                .anyMatch(u -> u.getEmail().equals(email));

        if (emailExists) {
            throw new DuplicateEmailException(String.format("Пользователь с email %s " +
                    "уже существует.", email));
        }
    }

    private void validateUserEmailWhenUpdate(Long excludeUserId, String email) {
        boolean emailExists = getAll().stream()
                .filter(u -> !u.getId().equals(excludeUserId))
                .anyMatch(u -> u.getEmail().equals(email));

        if (emailExists) {
            throw new DuplicateEmailException(String.format("Пользователь с email %s " +
                    "уже существует.", email));
        }
    }
}