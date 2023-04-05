package ru.practicum.shareit.UserTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    private UserDto john;
    private UserDto jane;

    @BeforeEach
    void beforeEach() {
        john = new UserDto(1L, "John", "john@doe.com");
        jane = new UserDto(2L, "Jane", "jane@doe.com");
    }

    @Test
    @SneakyThrows
    void createUser() {
        Mockito.when(userService.createUser(any())).thenReturn(jane);
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(jane))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(jane.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(jane.getName())));
    }

    @Test
    @SneakyThrows
    void updateUser() {
        User newUser = new User();
        newUser.setId(jane.getId());
        newUser.setName(jane.getName());
        Mockito.when(userService.updateUser(anyLong(), any())).thenReturn(jane);
        mockMvc.perform(patch("/users/{id}", 2)
                        .content(mapper.writeValueAsString(newUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(jane.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(jane.getName())));
    }

    @Test
    @SneakyThrows
    void getUser() {
        Mockito.when(userService.getUser(anyLong())).thenReturn(john);
        mockMvc.perform(get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(john.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(john.getName())));
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        mockMvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getUsers() {
        Mockito.when(userService.getUsers()).thenReturn(List.of(john, jane));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)));
    }

}
