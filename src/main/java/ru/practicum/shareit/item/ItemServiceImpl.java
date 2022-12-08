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


    @Override
    public ItemDto createItem(ItemDto itemDto) {
        if (!itemDto.getAvailable()) {
            log.warn("Нельзя создать вещь недоступной");
            throw new NotAvailableItemException();
        }
        if (userRepository.getUsers().values().stream().noneMatch(x -> x.getId() == itemDto.getOwner().getId())) {
            log.warn("Пользователья с айди " + itemDto.getOwner().getName() + " не существует, невозможно создать вещь");
            throw new UserNotFoundException();
        }
        log.debug("Создана вещь :" + itemDto.getName());
        return itemRepository.createItem(ItemMapper.INSTANCE.toModel(itemDto));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {

        if (itemRepository.getItems().get(itemDto.getId()).getOwner().getId() != itemDto.getOwner().getId()) {
            log.warn("Пользователья с айди " + itemDto.getOwner().getName() + " не существует, невозможно обновить вещь");
            throw new UserNotFoundException();
        }
        log.debug("Обновлена вещь с id: " + itemDto.getId());
        return itemRepository.updateItem(ItemMapper.INSTANCE.toModel(itemDto));
    }

    @Override
    public ItemDto getItem(long itemId) {
        log.debug("Получена вещь с id: " + itemId);
        return itemRepository.getItem(itemId);
    }

    @Override
    public List<ItemDto> getItemsByUser(long userId) {
        log.debug("Получен список вещей у пользователя с id: " + userId);
        return itemRepository.getItemsByUser(userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.debug("Получен список вещей содержавших символы:" + text);
        return itemRepository.searchItem(text);
    }
}
