package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UserBookingDto;

import java.util.List;

public interface BookingSerivce {

    UserBookingDto createBooking(Long userId, BookingDto bookingDto);

    UserBookingDto approve(Long userId, Long id, Boolean approve);

    UserBookingDto getBooking(Long userId, Long bookingId);

    List<UserBookingDto> getBookingsByBooker(Long userId, State state);

    List<UserBookingDto> getBookingsByOwner(Long ownerId, State state);

}
