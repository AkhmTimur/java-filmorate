package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.QueryAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film addToFilms(Film film) {
        String sql = "INSERT INTO films (film_name, release_date, description, duration, mpa) VALUES (?,?,?,?,?);";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setDate(2, Date.valueOf(film.getReleaseDate().toString()));
            stmt.setString(3, film.getDescription());
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, kh);
        film.setId(Objects.requireNonNull(kh.getKey()).longValue());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addToFilmGenres(film);
        }
        log.debug("Фильм {} добавлен", film.getName());

        return film;
    }

    @Override
    public Film putToFilm(Film film) {
        if (getFilm(film.getId()).isPresent()) {
            String sql = "UPDATE films SET film_name = ?, description = ?, release_date = ?," +
                    " duration = ?, mpa = ? WHERE film_id = ?";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

            deleteGenres(film);
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                addToFilmGenres(film);
            }
            log.debug("Данные пользователя name: {} id: {} обновлены", film.getName(), film.getId());
            return film;
        } else {
            throw new DataNotFoundException("Данного фильма нет в записях");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> result = new ArrayList<>();
        String sql = "SELECT * FROM films";
        jdbcTemplate.query(sql, (rs, rowNum) -> result.add(makeFilm(rs)));
        return result;
    }

    @Override
    public void likeToFilm(Long id, Long userId) {
        int count = checkRowsCount(id, userId);
        if (count != 0) {
            throw new QueryAlreadyExistException("Пользователь " + userId + " уже поставил лайк фильму " + id);
        }
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, userId);
        String countSql = "SELECT count(user_id) user_count  FROM film_likes WHERE film_id = ?";
        int userCount = jdbcTemplate.query(countSql, (rs, rowNum) -> rs.getInt("user_count") ,id).get(0);
        String updateFilm = "UPDATE films SET rate = ? WHERE film_id = ?";
        jdbcTemplate.update(updateFilm, userCount, id);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        int count = checkRowsCount(id, userId);
        if (count == 0) {
            throw new DataNotFoundException("Пользователь " + userId + " не ставил лайк фильму " + id);
        }
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);

    }

    @Override
    public Optional<Film> getFilm(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM films WHERE film_id = ?",
                    (rs, rowNum) -> Optional.of(makeFilm(rs)),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
            throw new DataNotFoundException("Данного фильма нет в записях " + id);
        }
    }

    @Override
    public void deleteFilm(Long id) {
        String preSql = "SELECT count(*) film_count FROM films WHERE film_id = ?";
        List<Integer> count = jdbcTemplate.query(preSql, (rs, rowNum) -> rs.getInt("film_count"), id);
        if(count.get(0) != 0) {
            String sql = "DELETE FROM films WHERE film_id = ?";
            jdbcTemplate.update(sql, id);
        } else {
            throw new DataNotFoundException("Фильма " + id + " нет в записях");
        }
    }


    @Override
    public List<Film> getMostPopularFilms(int count) {
        List<Film> mostPopularFilms = new ArrayList<>();
        String sql = "SELECT * FROM films ORDER BY rate DESC LIMIT ?";
        jdbcTemplate.query(sql, (rs, rowNum) -> mostPopularFilms.add(makeFilm(rs)), count);

        List<Film> result = new ArrayList<>();
        if (mostPopularFilms.size() > 0) {
            result.addAll(mostPopularFilms);
        } else {
            List<Film> films = new ArrayList<>();
            String filmSql = "SELECT * FROM films ORDER BY film_id LIMIT ?";
            jdbcTemplate.query(filmSql,
                    (rs, rowNum) -> films.add(getFilm(rs.getLong("film_id")).orElse(null)),
                    count);
            result.addAll(films);
        }
        return result;
    }

    public void addToFilmGenres(Film film) {
        SortedSet<Genre> genres = new TreeSet<>((g1, g2) -> g1.getId() - g2.getId());
        genres.addAll(film.getGenres());
        film.setGenres(new ArrayList<>(genres));
        String sql = "INSERT INTO films_genres (film_id, genre_id) VALUES (?,?);";  
        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, film.getGenres().get(i).getId());
                    }
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                }

        );
    }




    private void deleteGenres(Film film) {
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", film.getId());
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long filmId = rs.getLong("film_id");
        Integer mpaId = rs.getInt("mpa");

        List<Mpa> mpa = mpaStorage.getFilmMpa(mpaId);
        List<Genre> genres = genreStorage.getFilmGenres(filmId);

        return new Film(
                filmId,
                rs.getString("film_name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                rs.getInt("rate"),
                genres,
                mpa.get(0),
                Collections.emptySet());
    }

    private int checkRowsCount(Long id, Long userId) {
        String preSql = "SELECT count(*) likes_count FROM film_likes WHERE film_id = ? AND user_id = ?";
        List<Integer> count = jdbcTemplate.query(preSql, (rs, rowNum) -> rs.getInt("likes_count"), id, userId);
        return count.get(0);
    }
}
