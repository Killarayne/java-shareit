package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingSerivce {

    Booking createBooking(Long userId, Booking booking);

    Booking approve(Long userId, Long id, Boolean approve);

    Booking getBooking(Long userId, Long bookingId);


    List<Booking> getBookingsByBooker(Long userId, State state);

    List<Booking> getBookingsByOwner(Long ownerId, State state);
}
