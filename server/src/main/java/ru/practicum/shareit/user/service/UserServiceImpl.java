package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User userToUpdate = getUserById(id);

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            userToUpdate.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            userToUpdate.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userToUpdate);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDto(users);
    }

    @Override
    public UserDto getById(Long id) {
        User user = getUserById(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        getById(id);
        userRepository.deleteById(id);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("User with ID: %d not found.", id)));
    }
}