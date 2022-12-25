package ru.practicum.shareit.exceptions;

public class UnsupportedStatusException extends RuntimeException {

    private final String error;

    public UnsupportedStatusException(String error) {
        this.error = error;
    }

}
