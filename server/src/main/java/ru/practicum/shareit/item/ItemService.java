package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UserCommentDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto);

    ItemDto updateItem(long id, ItemDto itemDto);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItems(Long userId, Pageable pageable);

    List<ItemDto> searchItem(String text, Pageable pageable);

    UserCommentDto createComment(Long userId, Long itemId, CommentDto commentDto);

}
