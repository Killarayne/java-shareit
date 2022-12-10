package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotAvailableItemException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;


    @Override
    public ItemDto createItem(ItemDto itemDto) {
        if (!itemDto.getAvailable()) {
            log.warn("Can't create an item unavailable");
            throw new NotAvailableItemException();
        }
        if (userRepository.getUsers().values().stream().noneMatch(x -> x.getId() == itemDto.getOwner().getId())) {
            log.warn("User with id " + itemDto.getOwner().getName() + " not exist, can't create item");
            throw new UserNotFoundException();
        }
        log.debug("Created item :" + itemDto.getName());
        return itemRepository.createItem(itemMapper.toModel(itemDto));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {

        if (itemRepository.getItems().get(itemDto.getId()).getOwner().getId() != itemDto.getOwner().getId()) {
            log.warn("User with id " + itemDto.getOwner().getName() + " not exist, can't create item");
            throw new UserNotFoundException();
        }
        log.debug("Updated item with id: " + itemDto.getId());
        return itemRepository.updateItem(itemMapper.toModel(itemDto));
    }

    @Override
    public ItemDto getItem(long itemId) {
        log.debug("Received item with id: " + itemId);
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<ItemDto> getItemsByUser(long userId) {
        log.debug("Received list of items by user id: " + userId);
        return itemRepository.getItemsByUser(userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.debug("Received a list of things containing symbols:" + text);
        return itemRepository.searchItem(text);
    }
}
