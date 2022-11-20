package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
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
@Qualifier
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addToFilms(Film film) {
        String sql = "INSERT INTO films (film_name, release_date, description, duration, rate, mpa) VALUES (?,?,?,?,?,?);";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setDate(2, Date.valueOf(film.getReleaseDate().toString()));
            stmt.setString(3, film.getDescription());
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
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
                    " duration = ?, rate = ?, mpa = ? WHERE film_id = ?";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());

            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                deleteGenres(film);
                film.setGenres(updateGenres(film));
            } else {
                deleteGenres(film);
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
            throw new DataNotFoundException("Данного фильма нет в записях");
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
    public Mpa getMpa(Integer id) {
        try {
            String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
            List<Mpa> mpa = jdbcTemplate.query(sql,
                    (rs, rowNum) -> Mpa.builder().id(rs.getInt("mpa_id"))
                            .name(rs.getString("mpa_name")).build(),
                    id
            );
            if (mpa.size() > 0) {
                return mpa.get(0);
            } else {
                throw new DataNotFoundException("Данного рейтинга нет в записях");
            }
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
            throw new DataNotFoundException("Данного рейтинга нет в записях");
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa_name")).build());
    }

    @Override
    public Genre getGenre(Integer id) {
        try {
            String sql = "SELECT * FROM genres WHERE genre_id = ?";
            List<Genre> genres = jdbcTemplate.query(sql,
                    (rs, rowNum) -> Genre.builder().id(rs.getInt("genre_id"))
                            .name(rs.getString("genre_name")).build(),
                    id);
            if (genres.size() > 0) {
                return genres.get(0);
            } else {
                throw new DataNotFoundException("Данного жанра нет в записях");
            }
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
            throw new DataNotFoundException("Данного жанра нет в записях");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> Genre.builder().id(rs.getInt("genre_id"))
                        .name(rs.getString("genre_name")).build());
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        List<Long> mostPopularFilmIds = new ArrayList<>();
        String sql = "SELECT  film_id, COUNT(*) users_likes FROM film_likes GROUP BY film_id ORDER BY users_likes DESC LIMIT ?";
        jdbcTemplate.query(sql, (rs, rowNum) -> mostPopularFilmIds.add(rs.getLong("film_id")), count);

        List<Film> result = new ArrayList<>();
        if (mostPopularFilmIds.size() > 0) {
            for (Long mostPopularFilmId : mostPopularFilmIds) {
                result.add(getFilm(mostPopularFilmId).orElse(null));
            }
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
        String sql = "INSERT INTO films_genres (film_id, genre_id) VALUES (?,?);";
        if (film.getGenres() != null) {
            for (int i = 0; i < film.getGenres().size(); i++) {
                jdbcTemplate.update(sql, film.getId(), film.getGenres().get(i).getId());
            }
        }
    }

    private List<Genre> getFilmGenres(Long filmId) {
        String genreSql = "SELECT fg.genre_id, g.genre_name FROM films_genres fg" +
                " JOIN genres g ON g.genre_id = fg.genre_id" +
                " WHERE film_id = ?";
        List<Genre> genres = new ArrayList<>();
        jdbcTemplate.query(genreSql,
                (rsa, rowNum) -> genres.add(Genre.builder().id(rsa.getInt("genre_id"))
                        .name(rsa.getString("genre_name")).build()),
                filmId);
        if (genres.size() > 0) {
            return genres;
        }
        return Collections.emptyList();
    }

    private List<Mpa> getFilmMpa(Integer mpaId) {
        String sql = "SELECT * FROM mpa where mpa_id = ?";
        List<Mpa> mpa = new ArrayList<>();
        jdbcTemplate.query(sql,
                (rsa, rowNum) -> mpa.add(Mpa.builder()
                        .id(rsa.getInt("mpa_id")).name(rsa.getString("mpa_name")).build()),
                mpaId);
        if (mpa.size() > 0) {
            return mpa;
        }
        return null;
    }

    private void deleteGenres(Film film) {
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", film.getId());
    }

    private List<Genre> updateGenres(Film film) {
        List<Genre> sortedList = new ArrayList<>();
        SortedSet<Genre> genreSet = new TreeSet<>((g1, g2) -> g1.getId() - g2.getId());
        genreSet.addAll(film.getGenres());
        for (Genre genre : genreSet) {
            String preGenreSql = "SELECT COUNT(*) count FROM films_genres WHERE film_id = ? AND genre_id = ?";
            List<Integer> count = jdbcTemplate.query(preGenreSql, (rs, rowNum) -> rs.getInt("count"), film.getId(), genre.getId());
            if (count.get(0) == 0) {
                jdbcTemplate.update("INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId()
                );
            }
            sortedList.add(genre);
        }
        return sortedList;
    }


    private Film makeFilm(ResultSet rs) throws SQLException {
        Long filmId = rs.getLong("film_id");
        Integer mpaId = rs.getInt("mpa");

        List<Mpa> mpa = getFilmMpa(mpaId);
        List<Genre> genres = getFilmGenres(filmId);

        if (mpa != null) {
            return new Film(
                    filmId,
                    rs.getString("film_name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    rs.getInt("rate"),
                    genres,
                    mpa.get(0),
                    null
            );
        } else {
            return new Film(
                    filmId,
                    rs.getString("film_name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    rs.getInt("rate"),
                    null
            );
        }

    }

    private int checkRowsCount(Long id, Long userId) {
        String preSql = "SELECT count(*) likes_count FROM film_likes WHERE film_id = ? AND user_id = ?";
        List<Integer> count = jdbcTemplate.query(preSql, (rs, rowNum) -> rs.getInt("likes_count"), id, userId);
        return count.get(0);
    }
}
