package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addToUsers(User user) {
        birthdayValidation(user);
        if (user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        log.debug("Пользователь {} добавлен", user);
        return userStorage.addToUsers(user);
    }

    public User putToUser(User user) {
        birthdayValidation(user);
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }

        if (userStorage.getUsers().contains(user)) {
            userStorage.putToUser(user);
            log.debug("Пользователь {} обновлен", user);
            return user;
        } else {
            throw new DataNotFoundException("Данного пользователя нет в записях");
        }
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
        if (userStorage.getUser(id) != null && userStorage.getUser(friendId) != null) {
            log.debug("К пользователю с id: {} в друзья добавлен пользователь с id: {}", id, friendId);
            userStorage.addFriend(id, friendId);
        }
    }

    public void deleteFriend(Long id, Long friendId) {
        if (userStorage.getUser(id) != null && userStorage.getUser(friendId) != null) {
            log.debug("У пользователя с id: {} из друзей удален пользователь с id: {}", id, friendId);
            userStorage.deleteFriend(id, friendId);
        }
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        log.debug("Запрошены общие друзья пользователя с id: {} и пользователя с id: {}", id, otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    private void birthdayValidation(User user) {
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Пользователь {} не прошел валидацию. Полные данные: {}", user.getLogin(), user);
            throw new ValidationException("Некорректный формат поля birthday");
        }
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }
}
