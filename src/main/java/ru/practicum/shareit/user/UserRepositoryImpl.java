package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepositoryImpl implements UserRepository {

    long generatedId = 0L;

    Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public UserDto createUser(User user) {
        user.setId(++generatedId);
        users.put(user.getId(), user);
        return UserMapper.INSTANCE.toItemDto(user);
    }

    @Override
    public UserDto updateUser(long userId, User user) {
        if (user.getName() != null) {
            users.get(userId).setName(user.getName());
        }
        if (user.getEmail() != null) {

            users.get(userId).setEmail(user.getEmail());
        }
        return UserMapper.INSTANCE.toItemDto(users.get(userId));
    }

    @Override
    public UserDto getUser(long userId) {
        return UserMapper.INSTANCE.toItemDto(users.get(userId));
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }


}
