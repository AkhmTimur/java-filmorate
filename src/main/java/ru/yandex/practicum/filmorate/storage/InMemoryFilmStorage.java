package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private Long nextId = 0L;
    private final HashMap<Long, Film> films = new HashMap<>();

    private final UserStorage userStorage;

    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Film addToFilms(Film film) {
        if (film.getId() == null) {
            film.setId(genId());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film putToFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return films.keySet().stream()
                .map(films::get)
                .collect(Collectors.toList());
    }

    @Override
    public void likeToFilm(Long id, Long userId) {
        if (films.containsKey(id) && userStorage.getUser(userId) != null) {
            films.get(id).getUsersLikes().add(userId);
        } else {
            throw new DataNotFoundException("Фильма с данным id " + id + " не найдено.");
        }
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        if (films.get(id).getUsersLikes().contains(userId)) {
            if (userStorage.getUser(userId) != null) {
                films.get(id).getUsersLikes().remove(userId);
            } else {
                throw new DataNotFoundException("Пользователя с данным id " + userId + " не найдено.");
            }
        } else {
            throw new DataNotFoundException("У фильма с данным id " + id + " не найдено лайка пользователя " + userId);
        }
    }

    @Override
    public Film getFilm(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new DataNotFoundException("Фильм с id: " + id + " не найден");
        }
    }

    @Override
    public void deleteFilm(Long id) {
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new DataNotFoundException("Данного фильма id: " + id + " нет в записях");
        }
    }

    public Long genId() {
        nextId++;
        return nextId;
    }
}
