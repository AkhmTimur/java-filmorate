package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film addToFilms(@Valid @RequestBody Film film) {
        return filmService.addToFilms(film);
    }

    @PutMapping("/films")
    public Film putToFilm(@Valid @RequestBody Film film) {
        return filmService.putToFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void likeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.likeToFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostLikedFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getMostLikedFilms(count);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilm(id);
    }
}
