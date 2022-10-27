package ru.yandex.practicum.filmorate.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class FilmServiceTest {

    @Mock
    private FilmService filmService;
    @Mock
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage(inMemoryUserStorage);
        filmService = new FilmService(inMemoryFilmStorage);
        userService = new UserService(inMemoryUserStorage);
    }

    @Test
    public void addCorrectFilm() {
        Film film = new Film();
        film.setId(0L);
        film.setName("film name");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2022, 2, 24));

        filmService.addToFilms(film);

        assertEquals(film, filmService.getFilm(0L));
    }

    @Test
    public void addEmptyIdFilm() {
        Film film = new Film();
        film.setName("film name");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2022, 2, 24));

        filmService.addToFilms(film);

        assertEquals(film, filmService.getFilm(1L));
    }

    @Test
    public void addFilmWithFailName() {
        Film film = new Film();
        film.setId(0L);
        film.setName("");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2022, 2, 24));

        try {
            filmService.addToFilms(film);
        } catch (ValidationException e) {
            assertEquals("Данные не корректны", e.getMessage());
        }
    }

    @Test
    public void addFilmWithFailDesc() {
        Film film = new Film();
        film.setId(0L);
        film.setName("film name");
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.");
        film.setReleaseDate(LocalDate.of(2022, 2, 24));

        try {
            filmService.addToFilms(film);
        } catch (ValidationException e) {
            assertEquals("Данные не корректны", e.getMessage());
        }
    }

    @Test
    public void addFilmWithFailReleaseDate() {
        Film film = new Film();
        film.setId(0L);
        film.setName("film name");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(1890, 3, 24));

        try {
            filmService.addToFilms(film);
        } catch (ValidationException e) {
            assertEquals("Данные не корректны", e.getMessage());
        }
    }

    @Test
    public void addFilmWithFailDuration() {
        Film film = new Film();
        film.setId(0L);
        film.setName("film name");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2022, 3, 24));
        film.setDuration(-200);

        try {
            filmService.addToFilms(film);
        } catch (ValidationException e) {
            assertEquals("Данные не корректны", e.getMessage());
        }
    }

    @Test
    public void filmUpdate() {
        Film film = new Film();
        film.setName("film name");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2022, 3, 24));
        film.setDuration(2000);
        filmService.addToFilms(film);
        film.setName("film update");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(1979, 4, 17));
        film.setDuration(100);

        filmService.putToFilm(film);

        assertEquals(film, filmService.getFilm(1L));
    }

    @Test
    public void filmUpdateUnknown() {
        Film film = new Film();
        film.setName("film name");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2022, 3, 24));
        film.setDuration(2000);
        filmService.addToFilms(film);
        film.setId(9999L);
        film.setName("film update");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(1979, 4, 17));
        film.setDuration(100);

        try {
            filmService.putToFilm(film);
        } catch (DataNotFoundException e) {
            assertEquals("Фильм с id: 9999 не найден", e.getMessage());
        }
    }

    @Test
    public void filmAddLike() {
        Film film = new Film();
        film.setName("film name");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2022, 3, 24));
        film.setDuration(2000);
        filmService.addToFilms(film);

        User user = new User();
        user.setBirthday(LocalDate.of(2021, 1, 1));
        user.setEmail("someEmail@mail.com");
        user.setName("userName");
        user.setLogin("login");
        userService.addToUsers(user);

        filmService.likeToFilm(film.getId(), user.getId());

        assertTrue(filmService.getFilm(film.getId()).getUsersLikes().contains(user.getId()));
    }

    @Test
    public void getMostLikedFilm() {
        Film film = new Film();
        film.setName("film name");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2022, 3, 24));
        film.setDuration(2000);
        filmService.addToFilms(film);

        User user = new User();
        user.setBirthday(LocalDate.of(2021, 1, 1));
        user.setEmail("someEmail@mail.com");
        user.setName("userName");
        user.setLogin("login");
        userService.addToUsers(user);

        filmService.likeToFilm(film.getId(), user.getId());

        assertEquals(1, filmService.getMostLikedFilms(1).size());
        assertEquals(film, filmService.getMostLikedFilms(1).get(0));
    }

    @Test
    public void deleteLike() {
        Film film = new Film();
        film.setName("film name");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2022, 3, 24));
        film.setDuration(2000);
        filmService.addToFilms(film);

        User user = new User();
        user.setBirthday(LocalDate.of(2021, 1, 1));
        user.setEmail("someEmail@mail.com");
        user.setName("userName");
        user.setLogin("login");
        userService.addToUsers(user);

        filmService.likeToFilm(film.getId(), user.getId());

        assertEquals(1, filmService.getMostLikedFilms(1).size());
        assertEquals(film, filmService.getMostLikedFilms(1).get(0));

        filmService.deleteLike(film.getId(), user.getId());

        assertEquals(0, filmService.getFilm(film.getId()).getUsersLikes().size());
    }

    @Test
    public void deleteLikeWrongUserId() {
        Film film = new Film();
        film.setName("film name");
        film.setDescription("film description");
        film.setReleaseDate(LocalDate.of(2022, 3, 24));
        film.setDuration(2000);
        filmService.addToFilms(film);

        User user = new User();
        user.setBirthday(LocalDate.of(2021, 1, 1));
        user.setEmail("someEmail@mail.com");
        user.setName("userName");
        user.setLogin("login");
        userService.addToUsers(user);

        filmService.likeToFilm(film.getId(), user.getId());

        assertEquals(1, filmService.getMostLikedFilms(1).size());
        assertEquals(film, filmService.getMostLikedFilms(1).get(0));

        try {
            filmService.deleteLike(film.getId(), -2L);
        } catch (DataNotFoundException e) {
            assertEquals("У фильма с данным id 1 не найдено лайка пользователя -2", e.getMessage());
        }
    }
}
