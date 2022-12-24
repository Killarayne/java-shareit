package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UserCommentDto;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Validated(Create.class) ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(false);
        }
        itemDto.setOwnerId(userId);

        return itemMapper.toItemDto(service.createItem(itemMapper.toModel(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        return itemMapper.toItemDto(service.updateItem(userId, itemMapper.toModel(itemDto)));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId) {
        return service.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(value = "text") String text) {
        return service.searchItem(text).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }


    @PostMapping("/{itemId}/comment")
    public UserCommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        return commentMapper.toUserCommentDto(service.createComment(userId, itemId, commentMapper.toCommentModel(commentDto)));
    }


}
