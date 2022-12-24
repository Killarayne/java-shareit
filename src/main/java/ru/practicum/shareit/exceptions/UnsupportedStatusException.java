package ru.practicum.shareit.exceptions;

public class UnsupportedStatusException extends RuntimeException {
    String error;

    public UnsupportedStatusException(String error) {
        this.error = error;
    }
}
