package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private Long nextId = 0L;
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public User addToUsers(User user) {
        if (user.getId() == null) {
            user.setId(genId());
        }
        users.put(user.getId(), user);
        log.debug("Пользователь {} добавлен", user.getLogin());
        return user;
    }

    @Override
    public User putToUsers(User user) {
        users.put(user.getId(), user);
        log.debug("Пользователь {} обновлен", user.getLogin());
        return user;
    }

    @Override
    public List<User> getUsers() {
        ArrayList<User> result = new ArrayList<>();
        for (Long userId : users.keySet()) {
            result.add(users.get(userId));
        }
        result.sort(Comparator.comparingLong(User::getId));
        return result;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (users.containsKey(friendId)) {
            users.get(userId).addFriend(friendId);
            users.get(friendId).addFriend(userId);
        } else {
            throw new DataNotFoundException("Пользователь с id:" + friendId + " не найден");
        }
    }

    @Override
    public Optional<User> deleteFriend(Long userId, Long friendId) {
        if (users.containsKey(friendId)) {
            users.get(userId).deleteFriend(friendId);
            users.get(friendId).deleteFriend(userId);
        }
        return Optional.of(users.get(userId));
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> result = new ArrayList<>();
        if (users.get(id).getFriends() != null) {
            for (Long friendId : users.get(id).getFriends()) {
                if (users.containsKey(otherId) && users.get(otherId).getFriends().contains(friendId)) {
                    if (users.containsKey(friendId)) {
                        result.add(users.get(friendId));
                    }
                }
            }
            return result;
        } else {
            return Collections.emptyList();
        }

    }

    @Override
    public Optional<User> getUser(Long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        } else {
            throw new DataNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<User> getAllFriends(Long id) {
        List<User> result = new ArrayList<>();
        for (Long friend : users.get(id).getFriends()) {
            result.add(users.get(friend));
        }
        return result;
    }

    @Override
    public void deleteUser(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new DataNotFoundException("Пользователь с id: " + id + " не найден");
        }
    }

    private Long genId() {
        nextId++;
        return nextId;
    }
}