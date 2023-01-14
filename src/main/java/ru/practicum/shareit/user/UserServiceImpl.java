package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
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
        Optional<User> updatedUser = repository.findById(userId);
        if (!updatedUser.isPresent()) {
            log.warn("User is not exist");
            throw new UserNotFoundException();
        }
        if (user.getEmail() != null)
            updatedUser.get().setEmail(user.getEmail());

        if (user.getName() != null)
            updatedUser.get().setName(user.getName());

        repository.save(updatedUser.get());
        log.debug("user with email " + updatedUser.get().getEmail() + " updated");
        return userMapper.toUserDto(updatedUser.get());
    }

    @Override
    public UserDto getUser(long userId) {
        Optional<User> user = repository.findById(userId);
        if (!user.isPresent()) {
            log.warn("User is not exist");
            throw new UserNotFoundException();
        }
        log.debug("Received user with id " + userId);
        return userMapper.toUserDto(user.get());
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
