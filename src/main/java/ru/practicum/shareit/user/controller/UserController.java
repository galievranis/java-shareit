package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        log.info("POST запрос на создание пользователя.");
        return userService.create(user);
    }

    @PatchMapping("{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @Valid @RequestBody UserDto userDto) {
        log.info("PATCH запрос на обновление пользователя с ID {}.", userId);
        return userService.update(userId, userDto);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("GET запрос на получение списка всех пользователей.");
        return userService.getAll();
    }

    @GetMapping("{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("GET запрос на получение пользвоателя с ID {}.", userId);
        return userService.getById(userId);
    }

    @DeleteMapping("{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.info("DELETE запрос на удаление пользователя c ID {}.", userId);
        userService.deleteById(userId);
    }
}