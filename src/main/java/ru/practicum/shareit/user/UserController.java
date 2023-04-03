package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    @GetMapping
    public List<User> get() {
        log.info("Запрос на получение списка всех пользователей.");
//        return userService.get();
        return null;
    }

    @GetMapping("{userId}")
    public User get(@PositiveOrZero @PathVariable Long userId) {
        log.info("Запрос на получение пользователя с ID = {}.", userId);
//        return userService.get(userId);
        return null;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Запрос на создание пользователя.");
//        return userService.create(user);
        return null;
    }

    @PatchMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Запрос PUT: update(User user) на изменение пользователя.");
//        return userService.update(user);
        return null;
    }

    @DeleteMapping("{userId}")
    public void remove(@PositiveOrZero @PathVariable Long userId) {
        log.info("Запрос на удаление пользователя с ID = {}.", userId);
//        userService.remove(userId);
    }

}
