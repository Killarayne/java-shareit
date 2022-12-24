package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User createUser(User user) {
        log.debug("User with email " + user.getEmail() + " created");
        return repository.save(user);
    }

    @Override
    public User updateUser(long userId, User user) {

        User updatedUser = repository.findById(userId).get();
        if (user.getEmail() != null)
            updatedUser.setEmail(user.getEmail());

        if (user.getName() != null)
            updatedUser.setName(user.getName());

        repository.save(updatedUser);
        log.debug("user with email " + updatedUser.getEmail() + " updated");
        return updatedUser;
    }

    @Override
    public User getUser(long userId) {
        Optional<User> user = repository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }
        log.debug("Received user with id " + userId);
        return user.get();
    }


    @Override
    public void deleteUser(long userId) {
        log.debug("Deleted user with id " + userId);
        repository.deleteById(userId);
    }

    @Override
    public List<User> getUsers() {
        log.debug("Received all users");
        return repository.findAll();
    }
}
