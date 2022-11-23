package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpa(Integer id) {
        try {
            String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
            List<Mpa> mpa = jdbcTemplate.query(sql,
                    (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")) ,
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

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
    }


    List<Mpa> getFilmMpa(Integer mpaId) {
        String sql = "SELECT * FROM mpa where mpa_id = ?";
        return jdbcTemplate.query(sql,
                (rsa, rowNum) -> new Mpa(rsa.getInt("mpa_id"), rsa.getString("mpa_name")),
                mpaId);
    }
}
