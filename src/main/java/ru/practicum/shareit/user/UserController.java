package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return userMapper.toUserDto(service.createUser(userMapper.toModel(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody @Validated({Update.class}) UserDto userDto) {
        return userMapper.toUserDto(service.updateUser(userId, userMapper.toModel(userDto)));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userMapper.toUserDto(service.getUser(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        service.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return service.getUsers().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }


}
