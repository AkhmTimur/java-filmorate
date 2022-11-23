package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
public class GenreController {

    private final FilmService filmService;

    public GenreController(FilmService filmService) {
        this.filmService = filmService;
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
}
