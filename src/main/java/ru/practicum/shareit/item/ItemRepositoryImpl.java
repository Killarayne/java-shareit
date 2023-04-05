package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemMapper itemMapper;

    private long generatedId = 0L;

    private final Map<Long, Item> items = new HashMap<>();


    @Override
    public Map<Long, Item> getItems() {
        return items;
    }

    @Override
    public ItemDto createItem(Item item) {
        item.setId(++generatedId);
        items.put(item.getId(), item);
        return itemMapper.toItemDto(item);
    }


    @Override
    public ItemDto updateItem(Item item) {

        if (item.getOwner() != null) {
            items.get(item.getId()).setOwner(item.getOwner());
        }

        if (item.getName() != null) {
            items.get(item.getId()).setName(item.getName());
        }
        if (item.getRequest() != null) {
            items.get(item.getId()).setRequest(item.getRequest());
        }
        if (item.getDescription() != null) {
            items.get(item.getId()).setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            items.get(item.getId()).setAvailable(item.getAvailable());
        }

        return itemMapper.toItemDto(items.get(item.getId()));
    }

    @Override
    public ItemDto getItem(long itemId) {
        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUser(long userId) {
        return items.values().stream().map(itemMapper::toItemDto).filter(x -> x.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (!text.equals("")) {
            return items.values().stream().map(itemMapper::toItemDto).filter(x -> x.getName().toLowerCase().contains(text.toLowerCase()) && x.getAvailable() ||
                    x.getDescription().toLowerCase().contains(text.toLowerCase()) && x.getAvailable()).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }


}
