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

    @Override
    public UserDto createUser(UserDto userDto) {
        if (repository.getUsers().values().stream().anyMatch(x -> x.getEmail().equals(userDto.getEmail()))) {
            log.warn("Пользовательн с email " + userDto.getEmail() + " уже существует");
            throw new UserAlreadyExistException();
        }
        log.debug("Пользователь с email " + userDto.getEmail() + " создан");
        return repository.createUser(UserMapper.INSTANCE.toModel(userDto));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        if (repository.getUsers().values().stream().anyMatch(x -> x.getEmail().equals(userDto.getEmail()))) {
            log.warn("Пользовательн с email " + userDto.getEmail() + " уже существует");
            throw new UserAlreadyExistException();
        }
        log.debug("Пользователь с email " + userDto.getEmail() + " обновлен");
        return repository.updateUser(userId, UserMapper.INSTANCE.toModel(userDto));
    }

    @Override
    public UserDto getUser(long userId) {
        log.debug("Получен пользователь с id " + userId);
        return repository.getUser(userId);
    }

    @Override
    public void deleteUser(long userId) {
        log.debug("Удален пользователь с id " + userId);
        repository.deleteUser(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        log.debug("Получены все пользователи");
        return repository.getUsers().values().stream().map(UserMapper.INSTANCE::toItemDto).collect(Collectors.toList());
    }
}
