package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    private final HashMap<Integer, User> users = new HashMap<>();
    @PostMapping("/users")
    public User addToFilms(@Valid @RequestBody User user) {
        userValidation(user);
        if(user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("Пользователь {} добавлен", user.getLogin());
        return user;
    }

    @PutMapping("/users")
    public User putToFilm(@Valid @RequestBody User user) {
        userValidation(user);
        if(user.getName() == null) {
            user.setName(user.getLogin());
        }
        if(users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Пользователь {} обновлен", user.getLogin());
        } else {
            throw new DataNotFoundException("Данного пользователя нет в записях");
        }
        return user;
    }

    @GetMapping("/users")
    public List<User> getFilms() {
        ArrayList<User> result = new ArrayList<>();
        for (Integer filmId : users.keySet()) {
            result.add(users.get(filmId));
        }
        result.sort(Comparator.comparingInt(User::getId));
        return result;
    }

    private void userValidation(User user) {
        emailValidation(user);
        loginValidation(user);
        birthdayValidation(user);
    }

    private void emailValidation(User user) {
        if(user.getEmail() == null) {
            log.debug("Пользователь {} не прошел валидацию. Полные данные: {}", user.getLogin(), user);
            throwExceptionType("email");
        }
    }

    private void loginValidation(User user) {
        if(user.getLogin() == null || user.getLogin().contains(" ")) {
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
