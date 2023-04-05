package ru.practicum.shareit.exceptions;

public class WrongBookingStatusException extends RuntimeException {
    public WrongBookingStatusException(String message) {
        super(message);
    }
}
