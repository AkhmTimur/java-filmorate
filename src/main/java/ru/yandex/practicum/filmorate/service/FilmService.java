package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final LocalDate filmsBirthday = LocalDate.of(1895, 12, 28);

    private final InMemoryFilmStorage inMemoryFilmStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Film addToFilms(Film film) {
        log.debug("Фильм {} добавлен", film.getName());
        filmValidation(film);
        return inMemoryFilmStorage.addToFilms(film);
    }

    public Film putToFilm(Film film) {
        filmValidation(film);
        return inMemoryFilmStorage.putToFilm(film);
    }

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public void likeToFilm(Long id, Long userId) {
        inMemoryFilmStorage.likeToFilm(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        inMemoryFilmStorage.deleteLike(id, userId);
    }

    public List<Film> getMostLikedFilms(int count) {
        List<Film> preResult = new ArrayList<>(inMemoryFilmStorage.getAllFilms());
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
        return result;
    }

    public Film getFilm(Long id) {
        return inMemoryFilmStorage.getFilm(id);
    }

    private void filmValidation(Film film) {
        if(film.getName().equals("") || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(filmsBirthday) || film.getDuration() < 0) {
            log.debug("Фильм {} не прошел валидацию. Полные данные: {}", film.getName(), film);
            throw new ValidationException("Данные не корректны");
        }
    }
}
