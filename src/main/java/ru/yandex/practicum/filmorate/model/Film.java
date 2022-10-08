package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    int id = 1;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
}
