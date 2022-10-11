package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.DoesntExistDataException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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
            throw new DoesntExistDataException("Данного пользователя нет в записях");
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
        if(user.getEmail() == null || !user.getEmail().contains("@")
                || user.getLogin() == null || user.getLogin().contains(" ")
                || user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())
        ) {
            log.debug("Пользователь {} не прошел валидацию. Полные данные: {}", user.getLogin(), user);
            throw new ValidationException("Данные не корректны");
        }
    }
}
