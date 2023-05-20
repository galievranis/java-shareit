package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.markers.Create;
import ru.practicum.shareit.util.markers.Update;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("POST request to add a user.");
        return userClient.create(userDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> update(
            @PathVariable Long userId,
            @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("PATCH request to update user with ID: {}.", userId);
        return userClient.update(userId, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("GET request to get all users.");
        return userClient.getAll();
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        log.info("GET request to get user with ID: {}.", userId);
        return userClient.getById(userId);
    }

    @DeleteMapping("{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.info("DELETE request to delete user with ID: {}.", userId);
        userClient.deleteById(userId);
    }
}
