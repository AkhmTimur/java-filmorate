package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final LocalDate filmsBirthday = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addToFilms(Film film) {
        log.debug("Фильм {} добавлен", film.getName());
        filmValidation(film);
        return filmStorage.addToFilms(film);
    }

    public Film putToFilm(Film film) {
        filmValidation(film);
        log.debug("Фильм {} обновлён", film.getName());
        return filmStorage.putToFilm(film);
    }

    public List<Film> getAllFilms() {
        log.debug("Запрошены фильмы {}", filmStorage.getAllFilms());
        return filmStorage.getAllFilms();
    }

    public void likeToFilm(Long id, Long userId) {
        log.debug("Фильму с id: {} добавлен лайк пользователя с id: {}", id, userId);
        filmStorage.likeToFilm(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        log.debug("У фильма с id: {} удален лайк пользователя с id: {}", id, userId);
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getMostLikedFilms(int count) {
        List<Film> preResult = new ArrayList<>(filmStorage.getAllFilms());
        preResult.sort((f1, f2) -> {
            return f2.getUsersLikes().size() - f1.getUsersLikes().size();
        });
        List<Film> result = new ArrayList<>(count);
        for (Film film : preResult) {
            if(result.size() == count) {
                break;
            } else {
                result.add(film);
            }
        }
        log.debug("Самые популярные фильмы: {}", result);
        return result;
    }

    public Film getFilm(Long id) {
        log.debug("Запрошен фильм с id: {}", id);
        return filmStorage.getFilm(id);
    }

    private void filmValidation(Film film) {
        if(film.getName().equals("") || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(filmsBirthday) || film.getDuration() < 0) {
            log.debug("Фильм {} не прошел валидацию. Полные данные: {}", film.getName(), film);
            throw new ValidationException("Данные не корректны");
        }
    }
}
