package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    private long generatedId = 0L;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public UserDto createUser(User user) {
        user.setId(++generatedId);
        users.put(user.getId(), user);
        return userMapper.toItemDto(user);
    }

    @Override
    public UserDto updateUser(long userId, User user) {
        if (user.getName() != null) {
            users.get(userId).setName(user.getName());
        }
        if (user.getEmail() != null) {

            users.get(userId).setEmail(user.getEmail());
        }
        return userMapper.toItemDto(users.get(userId));
    }

    @Override
    public UserDto getUser(long userId) {
        return userMapper.toItemDto(users.get(userId));
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }


}
