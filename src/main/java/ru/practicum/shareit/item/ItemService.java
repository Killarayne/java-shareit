package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {


    Item createItem(Item item);

    Item updateItem(long id, Item item);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItems(Long userId);

    List<Item> searchItem(String text);

    Comment createComment(Long userId, Long itemId, Comment comment);
}
