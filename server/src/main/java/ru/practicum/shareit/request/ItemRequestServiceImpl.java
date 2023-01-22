package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("User with id: " + userId + " not exist, can't create request");
            throw new UserNotFoundException();
        }
        ItemRequest itemRequest = itemRequestMapper.toItemRequestModel(itemRequestDto);
        itemRequest.setRequestor(user.get());
        itemRequest.setCreated(LocalDateTime.now());
        log.debug("Created request with id: " + itemRequestDto.getId());
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequestsByRequestor(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("User with id: " + userId + " not exist, can't find requests");
            throw new UserNotFoundException();
        }
        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository.findAllByRequestorId(userId).stream()
                .map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());

        itemRequestDtoList.stream().forEach(x -> x.setItems(itemRepository.findAllByRequestId(x.getId()).stream()
                .map(itemMapper::toItemDto).collect(Collectors.toList())));
        log.debug("Received list if requests by requestor");
        return itemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> getRequestsAll(Pageable pageable, long userId) {
        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository.findAll(pageable).stream()
                .filter(x -> x.getRequestor().getId() != userId)
                .map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        itemRequestDtoList.stream().forEach(x -> x.setItems(itemRepository.findAllByRequestId(x.getId()).stream()
                .map(itemMapper::toItemDto).collect(Collectors.toList())));
        log.debug("Received list if requests");
        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDto getRequest(long requestId, long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("User with id: " + userId + " not exist, can't get request");
            throw new UserNotFoundException();
        }

        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest.isPresent()) {
            ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest.get());
            itemRequestDto.setItems(itemRepository.findAllByRequestId(requestId).stream().map(itemMapper::toItemDto).collect(Collectors.toList()));
            log.debug("Received request with id: " + requestId);
            return itemRequestDto;
        } else {
            log.warn("Request with id: " + requestId + " not exist, can't get request");
            throw new ItemRequestNotFoundException();
        }
    }

}
