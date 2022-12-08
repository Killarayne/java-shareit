package ru.practicum.shareit.user;

import java.util.Map;

public interface UserRepository {


    Map<Long, User> getUsers();

    UserDto createUser(User user);

    UserDto updateUser(long userId, User user);

    UserDto getUser(long userId);

    void deleteUser(long userId);

}
