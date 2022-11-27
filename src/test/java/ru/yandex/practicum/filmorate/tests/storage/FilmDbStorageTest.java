package ru.yandex.practicum.filmorate.tests.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;


    private final Film film = new Film(1L, "film", "filmDescr",
            LocalDate.of(2022, 10, 10), 100, 4, new Mpa(2, null));

    @Test
    void addToFilmsAndGetFilmsTest() {
        filmDbStorage.addToFilms(film);

        assertEquals(LocalDate.of(2022, 10, 10),
                Objects.requireNonNull(filmDbStorage.getFilm(film.getId()).orElse(null)).getReleaseDate());
    }

    @Test
    void putToFilmTest() {
        filmDbStorage.addToFilms(film);
        film.setName("newFilm");
        filmDbStorage.putToFilm(film);

        assertEquals("newFilm",
                Objects.requireNonNull(filmDbStorage.getFilm(film.getId()).orElse(null)).getName());
    }

    @Test
    void getAllFilmsTest() {
        for (Film film : filmDbStorage.getAllFilms()) {
            filmDbStorage.deleteFilm(film.getId());
        }
        filmDbStorage.addToFilms(film);
        Film film1 = film;
        film1.setReleaseDate(LocalDate.of(2011, 5, 5));
        filmDbStorage.addToFilms(film1);

        assertEquals(2, filmDbStorage.getAllFilms().size());
    }

    @Test
    void addLikeToFilmTest() {
        User user = new User(1L, "email@email.com",
                "userLogin", "userName", LocalDate.of(2010, 10, 10));
        filmDbStorage.addToFilms(film);
        filmDbStorage.likeToFilm(film.getId(), user.getId());

        assertEquals(Collections.emptySet(), Objects.requireNonNull(filmDbStorage.getFilm(film.getId()).orElse(null))
                .getUsersLikes());
    }

    @Test
    void deleteUsersLikeToFilmTest() {
        filmDbStorage.addToFilms(film);
        User user = new User(1L, "email@email.com",
                "userLogin", "userName", LocalDate.of(2010, 10, 10));
        filmDbStorage.likeToFilm(film.getId(), user.getId());

        filmDbStorage.deleteLike(film.getId(), user.getId());

        assertEquals(Collections.emptySet(), Objects.requireNonNull(filmDbStorage.getFilm(film.getId()).orElse(null)).getUsersLikes());
    }

    @Test
    void deleteFilmTest() {
        filmDbStorage.addToFilms(film);
        Film film1 = film;
        film1.setReleaseDate(LocalDate.of(2011, 5, 5));
        filmDbStorage.addToFilms(film1);

        filmDbStorage.deleteFilm(film1.getId());

        assertFalse(filmDbStorage.getAllFilms().contains(film1));
    }

    @Test
    void getMpaTest() {
        assertEquals("PG", mpaStorage.getMpa(film.getMpa().getId()).getName());
    }

    @Test
    void getAllMpaTest() {
        assertEquals(5, mpaStorage.getAllMpa().size());
    }

    @Test
    void getGenreByIdTest() {
        assertEquals("Комедия", genreStorage.getGenre(1).getName());
    }

    @Test
    void getAllGenresTest() {
        assertEquals(6, genreStorage.getAllGenres().size());
    }

    @Test
    void getMostPopularFilmsTest() {
        filmDbStorage.addToFilms(film);
        User user = new User(1L, "email@email.com",
                "userLogin", "userName", LocalDate.of(2010, 10, 10));
        filmDbStorage.likeToFilm(film.getId(), user.getId());
        Film film1 = film;
        film1.setReleaseDate(LocalDate.of(2011, 5, 5));
        filmDbStorage.addToFilms(film1);
        filmDbStorage.likeToFilm(film1.getId(), user.getId());

        assertEquals(2, filmDbStorage.getMostPopularFilms(2).size());
        assertEquals(1, filmDbStorage.getMostPopularFilms(1).size());
    }
}
