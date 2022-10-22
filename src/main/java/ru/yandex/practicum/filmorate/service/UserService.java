package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private static Long nextId = 0L;
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addToUsers(User user) {
        userValidation(user);
        if (user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        if(user.getId() == null) {
            user.setId(genId());
        }
        return inMemoryUserStorage.addToUsers(user);
    }

    public User putToUser(User user) {
        userValidation(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return inMemoryUserStorage.putToUser(user);
    }

    public List<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    public User getUser(Long id) {
        return inMemoryUserStorage.getUser(id);
    }

    public List<User> getAllFriends(Long id) {
        return inMemoryUserStorage.getAllFriends(id);
    }

    public void addFriend(Long id, Long friendId) {
        inMemoryUserStorage.addFriend(id, friendId);
    }

    public User deleteFriend(Long id, Long friendId) {
        return inMemoryUserStorage.deleteFriend(id, friendId);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return inMemoryUserStorage.getCommonFriends(id, otherId);
    }

    private Long genId() {
        nextId++;
        return nextId;
    }

    private void userValidation(User user) {
        emailValidation(user);
        loginValidation(user);
        birthdayValidation(user);
    }

    private void emailValidation(User user) {
        if (user.getEmail() == null) {
            log.debug("Пользователь {} не прошел валидацию. Полные данные: {}", user.getLogin(), user);
            throwExceptionType("email");
        }
    }

    private void loginValidation(User user) {
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.debug("Пользователь {} не прошел валидацию. Полные данные: {}", user.getLogin(), user);
            throwExceptionType("login");
        }
    }

    private void birthdayValidation(User user) {
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Пользователь {} не прошел валидацию. Полные данные: {}", user.getLogin(), user);
            throwExceptionType("birthday");
        }
    }

    private void throwExceptionType(String type) {
        throw new ValidationException("Некорректный формат поля " + type);
    }

}
