package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InMemoryUserRepositoryTest {

    private final UserController userController;

    @Test
    public void shouldCreateUser() {
        User userToCreate = User.builder()
                .name("John")
                .email("john@email.com")
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john@email.com")
                .build();

        UserDto actualUser = userController.createUser(userToCreate);

        assertEquals(expectedUser, actualUser, "Пользователи не совпадают.");
    }

    @Test
    public void shouldUpdateUserById() {
        User userToCreate = User.builder()
                .name("John")
                .email("john@email.com")
                .build();

        userController.createUser(userToCreate);

        UserDto userToUpdate = UserDto.builder()
                .name("Jonathan")
                .email("jonathan@email.com")
                .build();

        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .name("Jonathan")
                .email("jonathan@email.com")
                .build();

        UserDto actualUser = userController.updateUser(1L, userToUpdate);

        assertEquals(expectedUser, actualUser, "Пользователи не совпадают.");
    }

    @Test
    public void shouldDeleteUserById() {
        User userToCreate = User.builder()
                .name("John")
                .email("john@email.com")
                .build();

        userController.createUser(userToCreate);
        userController.deleteUserById(1L);

        assertThrows(NoSuchElementException.class,
                () -> userController.getUserById(1L));
    }

    @Test
    public void shouldReturnAllUsers() {
        User firstUser = User.builder()
                .name("John")
                .email("john@email.com")
                .build();

        User secondUser = User.builder()
                .name("Alex")
                .email("alex@email.com")
                .build();

        userController.createUser(firstUser);
        userController.createUser(secondUser);

        List<UserDto> actualUsersList = userController.getAllUsers();
        List<UserDto> expectedUsersList = new ArrayList<>();
        expectedUsersList.add(UserMapper.toUserDto(firstUser));
        expectedUsersList.add(UserMapper.toUserDto(secondUser));

        assertEquals(expectedUsersList, actualUsersList, "Список пользователей не совпадает.");
    }

    @Test
    public void shouldReturnUserById() {
        User userToCreate = User.builder()
                .name("John")
                .email("john@email.com")
                .build();

        userController.createUser(userToCreate);

        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john@email.com")
                .build();

        UserDto actualUser = userController.getUserById(1L);

        assertEquals(expectedUser, actualUser, "Пользователи не совпадают.");
    }
}
