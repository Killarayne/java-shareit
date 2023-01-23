package ru.practicum.shareit.exceptions;

public class WrongTextOfCommentException extends RuntimeException {
    public WrongTextOfCommentException(String message) {
        super(message);
    }
}
