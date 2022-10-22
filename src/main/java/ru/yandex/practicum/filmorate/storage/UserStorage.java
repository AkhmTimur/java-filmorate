package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User addToUsers(User User);

    public User putToUser(User User);

    public List<User> getUsers();

    public void addFriend(Long userId, Long friendId);

    public List<User> getFriends(Long id);

    public User deleteFriend(Long userId, Long friendId);

    public List<User> getCommonFriends(Long id, Long otherId);
}
