package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.NotAvailableItemException;
import ru.practicum.shareit.exceptions.UserAlreadyExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExistUser(final UserAlreadyExistException e) {
        return new ErrorResponse("User already exists.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotExistUser(final UserNotFoundException e) {
        return new ErrorResponse("User not found.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAvailable(final NotAvailableItemException e) {
        return new ErrorResponse("Item is unavailable.");
    }
}
