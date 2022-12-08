package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {


    ItemDto createItem(ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto);

    ItemDto getItem(long itemId);

    List<ItemDto> getItemsByUser(long userId);

    List<ItemDto> searchItem(String text);
}
