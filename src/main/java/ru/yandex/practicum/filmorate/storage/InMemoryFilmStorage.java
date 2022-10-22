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

    @Override
    public Film addToFilms(Film film) {
        if(film.getId() == null) {
            film.setId(genId());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film putToFilm(Film film) {
        if(films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new DataNotFoundException("Данного фильма нет в записях");
        }
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
        if(films.containsKey(id)) {
            films.get(id).getUsersLikes().add(userId);
        }
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        if(films.get(id).getUsersLikes().contains(userId)) {
            films.get(id).getUsersLikes().remove(userId);
        } else {
            throw new DataNotFoundException("Фильма с данным id " + id + " не найдено.");
        }
    }

    public Film getFilm(Long id) {
        if(films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new DataNotFoundException("Фильм с id: " + id + " не найден");
        }
    }

    public Long genId() {
        nextId++;
        return nextId;
    }
}
