package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;

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
        if (repository.getUsers().values().stream().anyMatch(x -> x.getEmail().equals(userDto.getEmail()))) {
            log.warn("User with id: " + userDto.getEmail() + " already exists");
            throw new UserAlreadyExistException();
        }
        log.debug("User with email " + userDto.getEmail() + " created");
        return repository.createUser(userMapper.toModel(userDto));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        if (repository.getUsers().values().stream().anyMatch(x -> x.getEmail().equals(userDto.getEmail()))) {
            log.warn("User with email " + userDto.getEmail() + " already exists");
            throw new UserAlreadyExistException();
        }
        log.debug("user with email " + userDto.getEmail() + " updated");
        return repository.updateUser(userId, userMapper.toModel(userDto));
    }

    @Override
    public UserDto getUser(long userId) {
        log.debug("Received user with id " + userId);
        return repository.getUser(userId);
    }

    @Override
    public void deleteUser(long userId) {
        log.debug("Deleted user with id " + userId);
        repository.deleteUser(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        log.debug("Received all users");
        return repository.getUsers().values().stream().map(userMapper::toItemDto).collect(Collectors.toList());
    }
}
