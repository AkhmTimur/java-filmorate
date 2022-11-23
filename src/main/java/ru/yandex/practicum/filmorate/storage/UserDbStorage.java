package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<Optional<User>> users = new ArrayList<>();
        String sql = "select U.USER_ID user_id from USERS as U" +
                " inner join FRIENDSHIPS as F on U.USER_ID = F.SECOND_USER_ID" +
                " inner join FRIENDSHIPS as S on U.USER_ID = S.SECOND_USER_ID" +
                " where F.FIRST_USER_ID = ? and S.FIRST_USER_ID = ?";
        jdbcTemplate.query(sql,
                (rs, rowNum) -> users.add(getUser(rs.getLong("user_id"))),
                id,
                otherId
        );
        return users.stream()
                .map(o -> o.orElse(null))
                .collect(Collectors.toList());
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
        String sql = "SELECT SECOND_USER_ID AS USER_ID FROM FRIENDSHIPS WHERE FIRST_USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getUser(rs.getLong("USER_ID")).orElse(null), id);
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
