package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UserBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingSerivce bookingSerivce;

    @PostMapping
    public UserBookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingDto bookingDto) {
        return bookingSerivce.createBooking(userId, bookingDto);

    }

    @PatchMapping("/{bookingId}")
    public UserBookingDto approved(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                   @RequestParam(name = "approved") Boolean approve) {
        return bookingSerivce.approve(userId, bookingId, approve);

    }

    @GetMapping("/{bookingId}")
    public UserBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingSerivce.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public List<UserBookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestParam(defaultValue = "ALL") State state,
                                                   @Valid @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                   @Valid @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return bookingSerivce.getBookingsByOwner(ownerId, state, pageRequest);
    }

    @GetMapping
    public List<UserBookingDto> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "ALL") State state,
                                                    @Valid @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                    @Valid @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

        final PageRequest pageRequest = PageRequest.of(from / size, size);
        return bookingSerivce.getBookingsByBooker(userId, state, pageRequest);

    }

}
