package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addToUsers(User User);

    User putToUsers(User User);

    List<User> getUsers();

    void addFriend(Long userId, Long friendId);

    Optional<User> deleteFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long id, Long otherId);

    Optional<User> getUser(Long id);

    List<User> getAllFriends(Long id);

    void deleteUser(Long id);
}
