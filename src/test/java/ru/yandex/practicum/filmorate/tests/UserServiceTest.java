package ru.yandex.practicum.filmorate.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {

    @Mock
    private UserService userService;
    InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    public void beforeEach() {
        inMemoryUserStorage= new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
    }

    @Test
    public void addToUsers() {
        User user = new User();
        user.setLogin("user login");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        userService.addToUsers(user);

        assertEquals(user, userService.getUser(user.getId()));
    }

    @Test
    public void addToUsersFailLogin() {
        User user = new User();
        user.setLogin("");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        userService.addToUsers(user);

        assertEquals(user, userService.getUser(user.getId()));
    }

    @Test
    public void addToUsersFailEmail() {
        User user = new User();
        user.setLogin("");
        user.setName("user name");
        user.setEmail("mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        userService.addToUsers(user);

        assertEquals(user, userService.getUser(user.getId()));
    }

    @Test
    public void addToUsersFailBirthday() {
        User user = new User();
        user.setLogin("");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(2990, 1, 1));

        try{
            userService.addToUsers(user);
        }catch (ValidationException e) {
            assertEquals("Некорректный формат поля birthday", e.getMessage());
        }
    }

    @Test
    public void userUpdate() {
        User user = new User();
        user.setLogin("user login");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userService.addToUsers(user);

        user.setName("updated name");
        userService.putToUser(user);

        assertEquals("updated name", userService.getUser(user.getId()).getName());
    }

    @Test
    public void userUpdateUnknown() {
        User user = new User();
        user.setLogin("user login");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userService.addToUsers(user);

        user.setId(11L);
        user.setLogin("login");
        user.setName("updated name");
        user.setEmail("updateduser@mail.ru");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        try {
            userService.putToUser(user);
        } catch (DataNotFoundException e) {
            assertEquals("Данного пользователя нет в записях", e.getMessage());
        }
    }

    @Test
    public void getAllUsers() {
        User user = new User();
        user.setLogin("user login");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userService.addToUsers(user);
        User newUser = new User();
        newUser.setId(2L);
        newUser.setLogin("login");
        newUser.setName("name");
        newUser.setEmail("updateduser@mail.ru");
        newUser.setBirthday(LocalDate.of(2000, 1, 1));
        userService.addToUsers(newUser);

        assertEquals(2, userService.getUsers().size());
        assertEquals(newUser, userService.getUsers().get(1));
    }

    @Test
    public void addFriend() {
        User user = new User();
        user.setLogin("user login");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userService.addToUsers(user);
        User newUser = new User();
        newUser.setId(2L);
        newUser.setLogin("login");
        newUser.setName("name");
        newUser.setEmail("updateduser@mail.ru");
        newUser.setBirthday(LocalDate.of(2000, 1, 1));
        userService.addToUsers(newUser);
        userService.addFriend(1L, 2L);

        assertEquals(List.of(newUser), userService.getAllFriends(1L));
        assertEquals(List.of(user), userService.getAllFriends(2L));
    }

    @Test
    public void commonFriend() {
        User user = new User();
        user.setLogin("user login");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userService.addToUsers(user);
        User newUser = new User();
        newUser.setLogin("login");
        newUser.setName("name");
        newUser.setEmail("updateduser@mail.ru");
        newUser.setBirthday(LocalDate.of(2000, 1, 1));
        userService.addToUsers(newUser);
        userService.addFriend(1L, 2L);
        User thirdUser = new User();
        thirdUser.setLogin("third");
        thirdUser.setName("thirdName");
        thirdUser.setEmail("third@mail.ru");
        thirdUser.setBirthday(LocalDate.of(2000, 1, 1));
        userService.addToUsers(thirdUser);
        userService.addFriend(3L, 2L);

        assertEquals(List.of(newUser), userService.getCommonFriends(1L, 3L));
    }

    @Test
    public void commonFriendUnknown() {
        User user = new User();
        user.setLogin("user login");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userService.addToUsers(user);
        User newUser = new User();
        newUser.setLogin("login");
        newUser.setName("name");
        newUser.setEmail("updateduser@mail.ru");
        newUser.setBirthday(LocalDate.of(2000, 1, 1));
        userService.addToUsers(newUser);
        userService.addFriend(user.getId(), newUser.getId());
        User thirdUser = new User();
        thirdUser.setLogin("third");
        thirdUser.setName("thirdName");
        thirdUser.setEmail("third@mail.ru");
        thirdUser.setBirthday(LocalDate.of(2000, 1, 1));
        userService.addToUsers(thirdUser);
        userService.addFriend(user.getId(), thirdUser.getId());

        assertEquals(Collections.emptyList(), userService.getCommonFriends(1L, -1L));
    }

    @Test
    public void getAllFriends() {
        User user = new User();
        user.setLogin("user login");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userService.addToUsers(user);
        User newUser = new User();
        newUser.setLogin("login");
        newUser.setName("name");
        newUser.setEmail("updateduser@mail.ru");
        newUser.setBirthday(LocalDate.of(2000, 1, 1));
        userService.addToUsers(newUser);
        userService.addFriend(1L, 2L);
        User thirdUser = new User();
        thirdUser.setLogin("third");
        thirdUser.setName("thirdName");
        thirdUser.setEmail("third@mail.ru");
        thirdUser.setBirthday(LocalDate.of(2000, 1, 1));
        userService.addToUsers(thirdUser);
        userService.addFriend(1L, 3L);

        assertEquals(List.of(newUser, thirdUser), userService.getAllFriends(1L));
    }

    @Test
    public void removeFriend() {
        User user = new User();
        user.setLogin("user login");
        user.setName("user name");
        user.setEmail("user@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userService.addToUsers(user);
        User newUser = new User();
        newUser.setLogin("login");
        newUser.setName("name");
        newUser.setEmail("updateduser@mail.ru");
        newUser.setBirthday(LocalDate.of(2000, 1, 1));
        userService.addToUsers(newUser);
        userService.addFriend(1L, 2L);
        User thirdUser = new User();
        thirdUser.setLogin("third");
        thirdUser.setName("thirdName");
        thirdUser.setEmail("third@mail.ru");
        thirdUser.setBirthday(LocalDate.of(2000, 1, 1));
        userService.addToUsers(thirdUser);
        userService.addFriend(1L, 3L);

        assertEquals(List.of(newUser, thirdUser), userService.getAllFriends(1L));

        userService.deleteFriend(1L, 3L);

        assertEquals(List.of(newUser), userService.getAllFriends(1L));
    }
}
