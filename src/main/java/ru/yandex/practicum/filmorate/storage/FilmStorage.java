package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film addToFilms(Film film);

    public Film putToFilm(Film film);

    public List<Film> getAllFilms();

    public void likeToFilm(Long id, Long userId);

    public void deleteLike(Long id, Long userId);
}
