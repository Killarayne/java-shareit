package ru.practicum.shareit.exceptions;

public class BookingNotExistException extends RuntimeException {

    public BookingNotExistException(String message) {
        super(message);
    }

}
