package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UserBookingDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingSerivce bookingSerivce;
    private final BookingMapper bookingMapper;

    @PostMapping
    public UserBookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingDto bookingDto) {
        return bookingMapper.toUserBookingDto(bookingSerivce.createBooking(userId, bookingMapper.toBookingModel(bookingDto)));

    }

    @PatchMapping("/{bookingId}")
    public UserBookingDto approved(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                   @RequestParam(name = "approved") Boolean approve) {
        return bookingMapper.toUserBookingDto(bookingSerivce.approve(userId, bookingId, approve));

    }

    @GetMapping("/{bookingId}")
    public UserBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingMapper.toUserBookingDto(bookingSerivce.getBooking(userId, bookingId));
    }


    @GetMapping("/owner")
    public List<UserBookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestParam(defaultValue = "ALL") State state) {
        return bookingSerivce.getBookingsByOwner(ownerId, state).stream().map(bookingMapper::toUserBookingDto).
                collect(Collectors.toList());
    }


    @GetMapping
    public List<UserBookingDto> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") State state) {
        return bookingSerivce.getBookingsByBooker(userId, state).stream().map(bookingMapper::toUserBookingDto).
                collect(Collectors.toList());

    }
}
