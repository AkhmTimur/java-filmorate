package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    @NotBlank
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private LocalDate releaseDate;
    private int duration;
    private Set<Long> usersLikes = new HashSet<>();

    public void addLikeToFilm(Long userId) {
        usersLikes.add(userId);
    }

    public void deleteLike(Long userId) {
        usersLikes.remove(userId);
    }
}
