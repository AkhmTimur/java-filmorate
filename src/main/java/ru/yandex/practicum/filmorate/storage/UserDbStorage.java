package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addToUsers(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        String sql = "INSERT INTO users (email, login, birthday, user_name) VALUES (?,?,?,?);";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setDate(3, Date.valueOf(user.getBirthday().toString()));
            stmt.setString(4, user.getName());
            return stmt;
        }, kh);
        user.setId(kh.getKey().longValue());
        return user;
    }

    @Override
    public User putToUsers(User user) {
        if (getUser(user.getId()).isPresent()) {
            String sql = "UPDATE users SET email = ?, login = ?, birthday = ?, user_name = ? WHERE user_id = ?";
            jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getBirthday(), user.getName(), user.getId());
            return user;
        } else {
            throw new DataNotFoundException("Данного пользователя нет в записях");
        }
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        jdbcTemplate.query(sql, (rs, rowNum) -> users.add(makeUser(rs)));
        return users;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String newQuery = "INSERT INTO friendships (first_user_id, second_user_id) VALUES (?, ?)";
        jdbcTemplate.update(newQuery, userId, friendId);
    }

    @Override
    public Optional<User> deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE first_user_id = ? AND second_user_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        return getUser(userId);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.* FROM users AS u" +
                " INNER JOIN friendships AS f ON u.user_id = f.second_user_id" +
                " INNER JOIN friendships AS s ON u.user_id = s.second_user_id" +
                " WHERE f.first_user_id = ? AND s.first_user_id = ?";
        jdbcTemplate.query(sql,
                (rs, rowNum) -> users.add(makeUser(rs)),
                id,
                otherId
        );
        return new ArrayList<>(users);
    }

    @Override
    public Optional<User> getUser(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE user_id = ?",
                    (rs, rowNum) ->
                            Optional.of(makeUser(rs))
                    , id
            );
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
            throw new DataNotFoundException("Данного пользователя нет в записях");
        }
    }

    @Override
    public List<User> getAllFriends(Long id) {
        String sql = "SELECT u.* " +
                " FROM friendships f" +
                " JOIN users u ON f.second_user_id = u.user_id" +
                " WHERE first_user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public void deleteUser(Long id) {
        String sql = "DELETE FROM users where user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}
