package ru.practicum.shareit.exceptions;

public class IncorrectCreatorOfComment extends RuntimeException {
    public IncorrectCreatorOfComment(String message) {
        super(message);
    }
}
