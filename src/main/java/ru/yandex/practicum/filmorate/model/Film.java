package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
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
    private Integer rate;
    private List<Genre> genres;
    private Mpa mpa;

    private Set<Long> usersLikes = new HashSet<>();

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration, Integer rate, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
    }

    public void addLikeToFilm(Long userId) {
        usersLikes.add(userId);
    }

    public void deleteLike(Long userId) {
        usersLikes.remove(userId);
    }
}
