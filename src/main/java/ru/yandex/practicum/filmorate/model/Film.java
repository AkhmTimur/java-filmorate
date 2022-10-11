package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    int id = 1;
    @NotBlank
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
}
