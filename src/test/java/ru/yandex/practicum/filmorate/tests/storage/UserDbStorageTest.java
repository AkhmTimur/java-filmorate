package ru.yandex.practicum.filmorate.tests.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    private final User user = new User(1L, "email@email.com",
            "userLogin", "userName", LocalDate.of(2010, 10, 10));


    @Test
    public void addToUsersCorrectTest() {
        userDbStorage.addToUsers(user);

        assertEquals(LocalDate.of(2010, 10, 10),
                Objects.requireNonNull(userDbStorage.getUser(1L).orElse(null)).getBirthday());
    }

    @Test
    void addToUsersEmptyNameTest() {
        user.setName(null);

        userDbStorage.addToUsers(user);

        assertEquals(user.getLogin(), Objects.requireNonNull(userDbStorage.getUser(user.getId()).orElse(null)).getName());
    }

    @Test
    void updateUserAndGetUserTest() {
        user.setEmail("email@email.ru");

        userDbStorage.putToUsers(user);

        assertEquals("email@email.ru",
                Objects.requireNonNull(userDbStorage.getUser(user.getId()).orElse(null)).getEmail());
    }

    @Test
    void userUpdateWrongIdTest() {
        user.setId(9999L);

        try {
            userDbStorage.putToUsers(user);
        } catch (DataNotFoundException e) {
            assertEquals("Данного пользователя нет в записях", e.getMessage());
        }
    }

    @Test
    void getAllUsersTest() {
        User user2 = user;
        user2.setEmail("user2@mail.ru");
        user2.setBirthday(LocalDate.of(2020, 2, 1));
        userDbStorage.addToUsers(user);
        userDbStorage.addToUsers(user2);

        assertEquals(3, userDbStorage.getUsers().size());
    }

    @Test
    void addFriendAndGetAllFriendTest() {
        User user2 = user;
        user2.setEmail("user2@mail.ru");
        user2.setBirthday(LocalDate.of(2020, 2, 1));
        userDbStorage.addToUsers(user);
        userDbStorage.addToUsers(user2);

        userDbStorage.addFriend(user.getId(), user2.getId());

        assertEquals("userName", userDbStorage.getAllFriends(user.getId()).get(0).getName());
    }

    @Test
    void addFriendAndDeleteFriendTest() {
        User user2 = user;
        user2.setEmail("user2@mail.ru");
        user2.setBirthday(LocalDate.of(2020, 2, 1));
        userDbStorage.addToUsers(user);
        userDbStorage.addToUsers(user2);
        userDbStorage.addFriend(user.getId(), user2.getId());
        assertEquals("userName", userDbStorage.getAllFriends(user.getId()).get(0).getName());

        userDbStorage.deleteFriend(user.getId(), user2.getId());

        assertEquals(0, userDbStorage.getAllFriends(user.getId()).size());
    }

    @Test
    void getCommonFriendTest() {
        User user2 = user;
        user2.setEmail("user2@mail.ru");
        user2.setBirthday(LocalDate.of(2020, 2, 1));
        User newUser = new User(3L, "user3@mail.ru", "newLogin", "newName", LocalDate.of(2000, 1, 2));
        userDbStorage.addToUsers(user);
        userDbStorage.addToUsers(user2);
        userDbStorage.addToUsers(newUser);
        userDbStorage.addFriend(user.getId(), newUser.getId());
        userDbStorage.addFriend(user2.getId(), newUser.getId());

        assertEquals(newUser, userDbStorage.getCommonFriends(user.getId(), user2.getId()).get(0));
    }

    @Test
    void deleteUserTest() {
        User user2 = user;
        user2.setEmail("user2@mail.ru");
        user2.setBirthday(LocalDate.of(2020, 2, 1));
        userDbStorage.addToUsers(user);
        userDbStorage.addToUsers(user2);
        assertTrue(userDbStorage.getUsers().contains(user));
        assertTrue(userDbStorage.getUsers().contains(user2));

        userDbStorage.deleteUser(user2.getId());

        assertFalse(userDbStorage.getUsers().contains(user2));
        assertEquals("userName", userDbStorage.getUsers().get(0).getName());
    }
}
