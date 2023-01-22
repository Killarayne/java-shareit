package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking requests with params was received in controller {} with data {}",
                this.getClass(),
                userId);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookingDto bookingDto) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.bookItem(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approved(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam(name = "approved") Boolean approve) {
        return bookingClient.approve(userId, bookingId, approve);
    }

}
