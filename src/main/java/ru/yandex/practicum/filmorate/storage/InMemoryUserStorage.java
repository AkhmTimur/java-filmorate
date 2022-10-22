package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public User addToUsers(User user) {
        users.put(user.getId(), user);
        log.debug("Пользователь {} добавлен", user.getLogin());
        return user;
    }

    @Override
    public User putToUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Пользователь {} обновлен", user.getLogin());
        } else {
            throw new DataNotFoundException("Данного пользователя нет в записях");
        }
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
        if(users.containsKey(friendId)) {
            users.get(userId).addFriend(friendId);
            users.get(friendId).addFriend(userId);
        } else {
            throw new DataNotFoundException("Пользователь с id:" + friendId + " не найден");
        }
    }

    @Override
    public List<User> getFriends(Long id) {
        List<User> result = new ArrayList<>();
        for (Long friendId : users.get(id).getFriends()) {
            result.add(users.get(friendId));
        }
        return result;
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        users.get(userId).deleteFriend(friendId);
        return users.get(userId);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> result = new ArrayList<>();
        if(users.get(id).getFriends() != null) {
            for (Long friendId : users.get(id).getFriends()) {
                if(users.get(otherId).getFriends().contains(friendId)) {
                    if(users.containsKey(friendId)) {
                        result.add(users.get(friendId));
                    }
                }
            }
            return result;
        } else {
            return Collections.emptyList();
        }

    }

    public User getUser(Long id) {
        if(users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new DataNotFoundException("Пользователь не найден");
        }
    }

    public List<User> getCommonFriend(Long id, Long friendId) {
        List<User> result = new ArrayList<>();
        for (Long friend : users.get(id).getFriends()) {
            if(users.get(friendId).getFriends().contains(friend)) {
                result.add(users.get(friend));
            }
        }
        return result;
    }

    public List<User> getAllFriends(Long id) {
        List<User> result = new ArrayList<>();
        for (Long friend : users.get(id).getFriends()) {
           result.add(users.get(friend));
        }
        return result;
    }
}