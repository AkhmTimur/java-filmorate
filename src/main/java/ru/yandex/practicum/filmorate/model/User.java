package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class User {
    int id = 1;
    @Email
    String email;
    @NotBlank
    String login;
    String name;
    LocalDate birthday;
}
