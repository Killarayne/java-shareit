package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemRepository {

    Map<Long, Item> getItems();

    ItemDto createItem(Item item);

    ItemDto updateItem(Item item);

    ItemDto getItem(long itemId);

    List<ItemDto> getItemsByUser(long userId);

    List<ItemDto> searchItem(String text);


}
