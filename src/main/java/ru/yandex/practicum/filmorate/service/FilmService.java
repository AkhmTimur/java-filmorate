package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final LocalDate filmsBirthday = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("FilmDbStorage")FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addToFilms(Film film) {
        filmValidation(film);
        filmStorage.addToFilms(film);
        log.debug("Фильм {} добавлен", film.getName());
        return film;
    }

    public Film putToFilm(Film film) {
        filmValidation(film);
        filmStorage.putToFilm(film);
        log.debug("Фильм {} обновлён", film.getName());
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        log.debug("Запрошены фильмы {}", filmStorage.getAllFilms());
        return films;
    }

    public void likeToFilm(Long id, Long userId) {
        filmStorage.likeToFilm(id, userId);
        log.debug("Фильму с id: {} добавлен лайк пользователя с id: {}", id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        filmStorage.deleteLike(id, userId);
        log.debug("У фильма с id: {} удален лайк пользователя с id: {}", id, userId);
    }

    public List<Film> getMostLikedFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }

    public Film getFilm(Long id) {
        Film film = filmStorage.getFilm(id).orElse(null);
        log.debug("Запрошен фильм с id: {}", id);
        return film;
    }

    public void deleteLFilm(Long id) {
        if(filmStorage.getFilm(id).isPresent()) {
            filmStorage.deleteFilm(id);
        }
    }

    private void filmValidation(Film film) {
        if(film.getName().equals("") || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(filmsBirthday) || film.getDuration() < 0) {
            log.debug("Фильм {} не прошел валидацию. Полные данные: {}", film.getName(), film);
            throw new ValidationException("Данные не корректны");
        }
    }
}