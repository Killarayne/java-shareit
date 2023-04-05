package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    List<ItemRequestDto> getRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getRequestsAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Valid @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                        @Valid @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return itemRequestService.getRequestsAll(pageRequest, userId);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getRequest(@PathVariable long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getRequest(requestId, userId);
    }

}
