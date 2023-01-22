package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotExistUser(final UserNotFoundException e) {
        return new ErrorResponse("User not found.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotExistItemRequest(final ItemRequestNotFoundException e) {
        return new ErrorResponse("ItemRequest not found.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotExistBooking(final BookingNotExistException e) {
        return new ErrorResponse("Booking not exist.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotExistItem(final ItemNotExistsException e) {
        return new ErrorResponse("Item is not exist");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAvailable(final NotAvailableItemException e) {
        return new ErrorResponse("Item is unavailable.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWrongTextOfComment(final WrongTextOfCommentException e) {
        return new ErrorResponse("Comment text can't be blank");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWrongBookingStatus(final WrongBookingStatusException e) {
        return new ErrorResponse("Wrong booking status.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWrongTime(final WrongTimeException e) {
        return new ErrorResponse("Wrong time.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectCreatorOfComment(final IncorrectCreatorOfComment e) {
        return new ErrorResponse("Incorrect creator of comment.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIllegalArgumentException() {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }

}
