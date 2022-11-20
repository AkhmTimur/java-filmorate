package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film addToFilms(@Valid @RequestBody Film film) {
        log.debug("Добавлен фильм: " + film);
        return filmService.addToFilms(film);
    }

    @PutMapping("/films")
    public Film putToFilm(@Valid @RequestBody Film film) {
        log.debug("Фильм: " + film + "обновлен");
        return filmService.putToFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.debug("Запрошены все фильмы");
        return filmService.getAllFilms();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void likeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Пользователь id: " + userId + " добавил лайк фильму filmId:" + id);
        filmService.likeToFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Пользователь id: " + userId + " удалил лайк фильму filmId:" + id);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostLikedFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.debug("Запрошены лучшие фильмы. Количество: " + count);
        return filmService.getMostLikedFilms(count);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        log.debug("Запрошен фильм. id: " + id);
        return filmService.getFilm(id);
    }

    @DeleteMapping("/films/{id}")
    public void deleteFilm(@PathVariable Long id) {
        log.debug("Удален фильм. id: " + id);
        filmService.deleteLFilm(id);
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(@PathVariable Integer id) {
        log.debug("Запрошен рейтинг: " + id);
        return filmService.getMpa(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpa() {
        log.debug("Запрошены все рейтинги");
        return filmService.getAllMpa();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable Integer id) {
        log.debug("Запрошен жанр: " + id);
        return filmService.getGenre(id);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.debug("Запрошены все жанры");
        return filmService.getAllGenres();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleDataNotFound(final DataNotFoundException e) {
        log.debug("Данные не найдены" + e.getMessage());
        return Map.of("Данные не найдены", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.debug("Запрос не поддерживается" + e.getMessage());
        return Map.of("Запрос не поддерживается", e.getMessage());
    }
}
