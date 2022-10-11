package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DoesntExistDataException extends RuntimeException  {
    String message;

    public DoesntExistDataException(String message) {
        this.message = message;
    }
}
