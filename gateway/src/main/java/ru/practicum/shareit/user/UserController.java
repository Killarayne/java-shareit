package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.debug("Created user with email: " + userDto.getEmail());
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @Validated({Update.class}) @RequestBody UserDto userDto) {
        log.debug("Updated user with id: "+ userId);
        return userClient.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.debug("Received user with id : " + userId);
        return userClient.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.debug("Deleted user with id:" + userId);
        userClient.deleteById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.debug("Received list of users");
        return userClient.getAll();
    }

}
