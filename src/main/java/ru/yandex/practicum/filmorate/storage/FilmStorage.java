package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addToFilms(Film film);

    Film putToFilm(Film film);

    List<Film> getAllFilms();

    void likeToFilm(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    Film getFilm(Long id);
}
