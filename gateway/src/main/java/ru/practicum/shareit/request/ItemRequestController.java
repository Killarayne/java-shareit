package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Created request by user id: " + userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Received request by user id: " + userId);
        return itemRequestClient.getAllByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                          @Valid @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.debug("Received all requests");
        return itemRequestClient.getRequestsAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Received request with id: " + requestId);
        return itemRequestClient.getById(userId, requestId);
    }

}
