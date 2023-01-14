package ru.practicum.shareit.UserTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        User john = new User(1L, "John", "john@doe.com");
        User jane = new User(2L, "Jane", "jane@doe.com");
        userRepository.save(john);
        userRepository.save(jane);
    }

    @Test
    void getUsers() {
        List<User> userList = userRepository.findAll();
        assertEquals(2, userList.size());
    }

}


