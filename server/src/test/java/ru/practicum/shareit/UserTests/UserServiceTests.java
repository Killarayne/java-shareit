package ru.practicum.shareit.UserTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.*;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest(classes = {UserServiceImpl.class, UserMapperImpl.class})
public class UserServiceTests {

    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    private User john;
    private User jane;

    @BeforeEach
    void beforeEach() {
        john = new User(1L, "John", "john@doe.com");
        jane = new User(2L, "Jane", "jane@doe.com");
    }

    @Test
    void getUser() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(john));
        User user = userMapper.toModel(userService.getUser(john.getId()));
        verify(userRepository, times(1)).findById(anyLong());
        assertThat(user, equalTo(user));
    }

    @Test
    void getUsers() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(john, jane));
        List<UserDto> users = userService.getUsers();
        verify(userRepository, times(1)).findAll();
        assertEquals(2, users.size());
    }

    @Test
    void createUser() {
        Mockito.when(userRepository.save(any())).thenReturn(john);
        UserDto user = userService.createUser(userMapper.toUserDto(john));
        verify(userRepository, times(1)).save(any());
        assertThat(user, equalTo(user));
    }

    @Test
    void getByWrongId() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUser(john.getId()));
    }

    @Test
    void update() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(john));
        UserDto user = userService.updateUser(1L, userMapper.toUserDto(john));
        verify(userRepository, times(1)).findById(anyLong());
        assertThat(user, equalTo(user));
    }

    @Test
    void updateNonExistentUser() {
        Mockito.when(userRepository.existsById(john.getId())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, userMapper.toUserDto(john)));
    }

    @Test
    void partialUpdate() {
        Mockito.when(userRepository.findById(john.getId())).thenReturn(Optional.ofNullable(john));
        Mockito.when(userRepository.save(any())).thenReturn(john);
        UserDto updateDto = new UserDto();
        updateDto.setId(john.getId());
        updateDto.setName(jane.getName());
        UserDto user = userService.updateUser(1L, updateDto);
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any());
        assertThat(user.getName(), equalTo(updateDto.getName()));
    }

    @Test
    void partialUpdateNonExistentUser() {
        Mockito.when(userRepository.findById(john.getId())).thenReturn(Optional.empty());
        UserDto userDto = new UserDto();
        userDto.setId(john.getId());
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, userDto));
    }

    @Test
    void delete() {
        Mockito.doNothing().when(userRepository).deleteById(john.getId());
        userService.deleteUser(john.getId());
        Mockito.verify(userRepository, times(1)).deleteById(anyLong());

    }

}
