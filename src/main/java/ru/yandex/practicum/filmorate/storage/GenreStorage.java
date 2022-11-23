package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql,
                (rs, rowNum) ->new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
    }

    public Genre getGenre(Integer id) {
        try {
            String sql = "SELECT * FROM genres WHERE genre_id = ?";
            List<Genre> genres = jdbcTemplate.query(sql,
                    (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")),
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

    List<Genre> getFilmGenres(Long filmId) {
        String genreSql = "SELECT fg.genre_id, g.genre_name FROM films_genres fg" +
                " JOIN genres g ON g.genre_id = fg.genre_id" +
                " WHERE film_id = ?";
        return jdbcTemplate.query(genreSql,
                (rsa, rowNum) -> new Genre(rsa.getInt("genre_id"), rsa.getString("genre_name")),
                filmId);
    }
}
