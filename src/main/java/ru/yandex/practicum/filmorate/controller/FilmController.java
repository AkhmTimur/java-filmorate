package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private final LocalDate filmsBirthDay = LocalDate.of(1895, 12, 28);

    @PostMapping("/films")
    public Film addToFilms(@Valid @RequestBody Film film) {
        filmValidation(film);
        films.put(film.getId(), film);
        log.debug("Фильм {} добавлен", film.getName());
        return film;
    }

    @PutMapping("/films")
    public Film putToFilm(@Valid @RequestBody Film film) {
        filmValidation(film);
        if(films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new DataNotFoundException("Данного фильма нет в записях");
        }
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return films.keySet().stream()
                .map(films::get)
                .collect(Collectors.toList());
    }

    private void filmValidation(Film film) {
        if(film.getName().equals("") || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(filmsBirthDay) || film.getDuration() < 0) {
            log.debug("Фильм {} не прошел валидацию. Полные данные: {}", film.getName(), film);
            throw new ValidationException("Данные не корректны");
        }
    }
}
