package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UserCommentDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ServerItemController {

    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(false);
        }
        itemDto.setOwnerId(userId);
        return service.createItem(itemDto);
    }

    @PutMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        return service.updateItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId) {
        return service.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                  @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return service.getItems(userId, pageRequest);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(value = "text") String text,
                                    @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return service.searchItem(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public UserCommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        return service.createComment(userId, itemId, commentDto);
    }

}
