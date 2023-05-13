package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserService tests")
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    @DisplayName("'create' should create user successfully")
    void createUser_Success() {
        User user = createUser1();

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto actualUser = userService.create(UserMapper.toUserDto(user));

        assertNotNull(actualUser);
        assertThat(actualUser.getId(), equalTo(user.getId()));
        assertThat(actualUser.getName(), equalTo(user.getName()));
        assertThat(actualUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("'update' should update user name and email successfully")
    void updateUser_Success() {
        User user = createUser1();
        Long id = user.getId();
        String newName = "Updated";
        String newEmail = "updatedUserEmail@mail.com";

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        User updatedUser = User.builder()
                .id(1L)
                .name(newName)
                .email(newEmail)
                .build();

        UserDto actualUser = userService.update(id, UserMapper.toUserDto(updatedUser));

        verify(userRepository, times(1)).findById(id);

        assertNotNull(actualUser);
        assertThat(actualUser.getId(), equalTo(id));
        assertThat(actualUser.getName(), equalTo(newName));
        assertThat(actualUser.getEmail(), equalTo(newEmail));
    }

    @Test
    @DisplayName("'update' should throw exception when user is not found")
    void updateUser_UserNotFound() {
        User user = createUser1();
        Long id = 2L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                userService.update(id, UserMapper.toUserDto(user)));

        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'update' should not update user name or email if they null or blank")
    void updateUser_EmptyNameOrEmail() {
        User user = createUser1();
        Long id = user.getId();
        String newName = " ";

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        User updatedUser = User.builder()
                .id(1L)
                .name(newName)
                .email(null)
                .build();

        UserDto actualUser = userService.update(id, UserMapper.toUserDto(updatedUser));

        verify(userRepository, times(1)).findById(id);

        assertNotNull(actualUser);
        assertThat(actualUser.getId(), equalTo(id));
        assertThat(actualUser.getName(), equalTo(user.getName()));
        assertThat(actualUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("'getAll' should return all users successfully")
    void getAllUsers_Success() {
        User user1 = createUser1();
        User user2 = createUser2();
        List<User> expectedUsers = List.of(user1, user2);

        when(userRepository.findAll())
                .thenReturn(expectedUsers);

        List<UserDto> actualUsers = userService.getAll();

        assertNotNull(actualUsers);
        assertThat(actualUsers.size(), equalTo(expectedUsers.size()));
    }

    @Test
    @DisplayName("'getById' should return user by ID successfully")
    void getUserById_Success() {
        User user = createUser1();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        UserDto actualUser = userService.getById(user.getId());

        assertNotNull(actualUser);
        assertThat(actualUser.getId(), equalTo(user.getId()));
        assertThat(actualUser.getName(), equalTo(user.getName()));
        assertThat(actualUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("'getById' should throw exception when user not found")
    void getUserById_UserNotFound() {
        Long id = 2L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                userService.getById(id));

        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'delete' should delete user by ID successfully")
    void deleteUserById_Success() {
        User user = createUser1();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        userService.deleteById(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }

    private User createUser1() {
        return User.builder()
                .id(1L)
                .name("User 1")
                .email("user1Email@mail.com")
                .build();
    }

    private User createUser2() {
        return User.builder()
                .id(2L)
                .name("User 2")
                .email("user2Email@mail.com")
                .build();
    }
}