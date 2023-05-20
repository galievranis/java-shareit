package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("POST request to add a user.");
        return userService.create(userDto);
    }

    @PatchMapping("{userId}")
    public UserDto update(
            @PathVariable Long userId,
            @RequestBody UserDto userDto) {
        log.info("PATCH request to update user with ID: {}.", userId);
        return userService.update(userId, userDto);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("GET request to get all users.");
        return userService.getAll();
    }

    @GetMapping("{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("GET request to get user with ID: {}.", userId);
        return userService.getById(userId);
    }

    @DeleteMapping("{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.info("DELETE request to delete user with ID: {}.", userId);
        userService.deleteById(userId);
    }
}