package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    int id = 1;
    String email;
    String login;
    String name;
    LocalDate birthday;
}
