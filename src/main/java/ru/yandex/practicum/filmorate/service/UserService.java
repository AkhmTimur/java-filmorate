package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private static Long nextId = 0L;
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addToUsers(User user) {
        userValidation(user);
        if (user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        if(user.getId() == null) {
            user.setId(genId());
        }
        log.debug("Пользователь {} добавлен", user);
        return userStorage.addToUsers(user);
    }

    public User putToUser(User user) {
        userValidation(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        log.debug("Пользователь {} обновлен", user);
        return userStorage.putToUser(user);
    }

    public List<User> getUsers() {
        log.debug("Запрошены пользователи {}", userStorage.getUsers());
        return userStorage.getUsers();
    }

    public User getUser(Long id) {
        log.debug("Запрошен пользователь {}", userStorage.getUser(id));
        return userStorage.getUser(id);
    }

    public List<User> getAllFriends(Long id) {
        log.debug("Запрошены друзья пользователя с id: {} : {}", id, userStorage.getAllFriends(id));
        return userStorage.getAllFriends(id);
    }

    public void addFriend(Long id, Long friendId) {
        log.debug("К пользователю с id: {} в друзья добавлен пользователь с id: {}", id, friendId);
        userStorage.addFriend(id, friendId);
    }

    public User deleteFriend(Long id, Long friendId) {
        log.debug("У пользователя с id: {} из друзей удален пользователь с id: {}", id, friendId);
        return userStorage.deleteFriend(id, friendId);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        log.debug("Запрошены общие друзья пользователя с id: {} и пользователя с id: {}", id, otherId);
        return userStorage.getCommonFriends(id, otherId);
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
