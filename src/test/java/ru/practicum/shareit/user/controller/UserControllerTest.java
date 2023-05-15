package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.entity.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.constants.RequestHeaderConstants.OWNER_ID_HEADER;

@DisplayName("UserController tests")
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("'create' should create user successfully")
    void createUser_Success() throws Exception {
        // given
        User user = createUser1();

        when(userService.create(any()))
                .thenReturn(UserMapper.toUserDto(user));

        // when
        mvc.perform(post("/users")
                        .header(OWNER_ID_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(UserMapper.toUserDto(user)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    @DisplayName("'update' should update user successfully")
    void updateUser_Success() throws Exception {
        // given
        User user = createUser1();

        when(userService.update(anyLong(), any()))
                .thenReturn(UserMapper.toUserDto(user));

        // when
        mvc.perform(patch("/users/{userId}", user.getId())
                        .content(objectMapper.writeValueAsString(UserMapper.toUserDto(user)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    @DisplayName("'getAll' should return all users successfully")
    void getAllUsers_Success() throws Exception {
        // given
        User user1 = createUser1();
        User user2 = createUser2();
        List<User> users = List.of(user1, user2);

        when(userService.getAll())
                .thenReturn(UserMapper.toUserDto(users));

        // when
        mvc.perform(get("/users"))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[1].id", is(user2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail())))
        ;
    }

    @Test
    @DisplayName("'getById' should return user by ID successfully")
    void getUserById_Success() throws Exception {
        // given
        User user = createUser1();

        when(userService.getById(anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        // when
        mvc.perform(get("/users/{userId}", user.getId()))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    @DisplayName("'delete' should delete user successfully")
    void deleteUserById_Success() throws Exception {
        // given
        User user = createUser1();

        // when
        mvc.perform(delete("/users/{userId}", user.getId()))
                // then
                .andExpect(status().isOk());
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