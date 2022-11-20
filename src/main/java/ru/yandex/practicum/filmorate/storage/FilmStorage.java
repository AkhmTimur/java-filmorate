package ru.yandex.practicum.filmorate.storage;

import org.springframework.data.relational.core.sql.In;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addToFilms(Film film);

    Film putToFilm(Film film);

    List<Film> getAllFilms();

    void likeToFilm(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    Optional<Film> getFilm(Long id);

    void deleteFilm(Long id);

    Mpa getMpa(Integer id);

    List<Mpa> getAllMpa();

    Genre getGenre(Integer id);

    List<Genre> getAllGenres();

    List<Film> getMostPopularFilms(int count);
}
