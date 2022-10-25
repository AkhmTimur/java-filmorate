package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addToUsers(User User);

    User putToUser(User User);

    List<User> getUsers();

    void addFriend(Long userId, Long friendId);

    User deleteFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long id, Long otherId);

    User getUser(Long id);

    List<User> getAllFriends(Long id);
}
