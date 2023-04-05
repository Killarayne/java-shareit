package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.debug("User with email " + userDto.getEmail() + " created");
        return userMapper.toUserDto(repository.save(userMapper.toModel(userDto)));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = userMapper.toModel(userDto);
        User updatedUser = repository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " is not exist"));
        if (user.getEmail() != null)
            updatedUser.setEmail(user.getEmail());

        if (user.getName() != null)
            updatedUser.setName(user.getName());

        repository.save(updatedUser);
        log.debug("user with email " + updatedUser.getEmail() + " updated");
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUser(long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " is not exist"));
        log.debug("Received user with id " + userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        log.debug("Deleted user with id " + userId);
        repository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        log.debug("Received all users");
        return repository.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

}
