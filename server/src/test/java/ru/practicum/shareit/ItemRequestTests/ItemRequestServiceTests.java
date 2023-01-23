package ru.practicum.shareit.ItemRequestTests;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {ItemRequestServiceImpl.class, ItemRequestMapperImpl.class, ItemMapperImpl.class})
public class ItemRequestServiceTests {

    @MockBean
    ItemRequestRepository itemRequestRepository;
    @MockBean
    ItemRepository itemRepository;
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    ItemRequestMapper itemRequestMapper;
    @MockBean
    UserRepository userRepository;
    @Autowired
    ItemRequestService itemRequestService;
    ItemRequestDto itemRequestDto = new ItemRequestDto(1, "test description", LocalDateTime.now(), null);
    Item item = new Item(1, "item", "description of item", true, 1L, 1L);
    private final User john = new User(1L, "John", "john@doe.com");
    ItemRequest itemRequest = new ItemRequest(1, "test description", john, LocalDateTime.now(), Collections.emptyList());

    @Test
    void createRequestIfUserNotExist() {
        Mockito.when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemRequestService.createRequest(itemRequestDto, john.getId()));
    }

    @Test
    void createRequest() {
        Mockito.when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        ItemRequestDto itemRequestDtoTwo = itemRequestService.createRequest(itemRequestDto, john.getId());
        verify(itemRequestRepository, times(1)).save(any());
        verify(userRepository, times(1)).findById(anyLong());
        assertThat(itemRequestDtoTwo, equalTo(itemRequestDtoTwo));
    }

    @Test
    void getRequestsByRequestorIfUserNotExist() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemRequestService.getRequestsByRequestor(john.getId()));
    }

    @Test
    void getRequestsByRequestor() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(itemRequestRepository.findAllByRequestorId(anyLong())).thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getRequestsByRequestor(john.getId());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAllByRequestorId(anyLong());
        assertThat(itemRequestDtoList, equalTo(itemRequestDtoList));
    }

    @Test
    void getRequestsAll() {
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest));
        Mockito.when(itemRequestRepository.findAll((Pageable) any())).thenReturn(page);
        Mockito.when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getRequestsAll(Pageable.ofSize(1), john.getId());
        verify(itemRequestRepository, times(1)).findAll((Pageable) any());
        assertThat(itemRequestDtoList, equalTo(itemRequestDtoList));
    }

    @Test
    void getRequestIfUserNotExist() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemRequestService.getRequest(itemRequest.getId(), john.getId()));
    }

    @Test
    void getRequestIfRequestNotExist() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getRequest(itemRequest.getId(), john.getId()));
    }

    @Test
    void getRequest() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestDto itemRequestDtoTwo = itemRequestService.getRequest(itemRequest.getId(), john.getId());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllByRequestId(anyLong());
        assertThat(itemRequestDtoTwo, equalTo(itemRequestDtoTwo));
    }

}
