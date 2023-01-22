package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Validated(Create.class) ItemDto itemDto) {
        log.debug("Created item by user id: "+ userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        log.debug("Updated item by user id: " + userId);
        return itemClient.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId) {
        log.debug("Received item with id: " + itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @Valid @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                           @Valid @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        log.debug("Received items by user with id: " + userId);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam(value = "text") String text,
                                             @Valid @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                             @Valid @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        log.debug("Search item with name or description contains: " + text);
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        log.debug("Created comment with text: " + commentDto.getText());
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
