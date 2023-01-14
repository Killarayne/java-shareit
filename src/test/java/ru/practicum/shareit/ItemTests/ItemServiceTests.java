package ru.practicum.shareit.ItemTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UserCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ItemServiceImpl.class, ItemMapper.class, ItemMapperImpl.class, ItemRepository.class, CommentMapperImpl.class, BookingMapperImpl.class})
public class ItemServiceTests {

    @Autowired
    ItemService itemService;
    Item item;
    ItemDto itemDto;
    User john;
    Comment comment;
    CommentDto commentDto;
    @MockBean
    private CommentRepository commentRepository;
    @Autowired
    private CommentMapper commentMapper;
    @MockBean
    private BookingRepository bookingRepository;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BookingMapper bookingMapper;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;
    private User jane;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        item = new Item(1, "item", "description of item", true, 1L, 1L);
        itemDto = new ItemDto(1L, "item", "description of item", true,
                null, null, 1L, null, null);

        booking = new Booking(1,LocalDateTime.now().plusHours(1),LocalDateTime.now().plusHours(1),item,jane,Status.WAITING);
        john = new User(1L, "John", "john@doe.com");
        comment = new Comment(1L, "nice item", john, item);
        commentDto = new CommentDto(1L, "nice item", john, item);
        jane = new User(2L, "Jane", "jane@doe.com");

    }

    @Test
    void createItemIfItemIsUnavailable() {
        itemDto.setAvailable(false);
        assertThrows(NotAvailableItemException.class, () -> itemService.createItem(itemDto));
    }

    @Test
    void createItemIfUserIsNotExists() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemService.createItem(itemDto));
    }

    @Test
    void createItem() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(itemRepository.save(any())).thenReturn(item);
        ItemDto secondItemDto = itemService.createItem(itemDto);
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
        assertThat(secondItemDto, equalTo(secondItemDto));
    }

    @Test
    void updateItemIfItemIsNotExist() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemNotExistsException.class, () -> itemService.updateItem(john.getId(), itemDto));
    }

    @Test
    void updateItemIfWrongUser() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(UserNotFoundException.class, () -> itemService.updateItem(2, itemDto));
    }

    @Test
    void updateItem() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(any())).thenReturn(item);
        ItemDto secondItemDto = itemService.updateItem(john.getId(), itemDto);
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
        assertThat(secondItemDto, equalTo(secondItemDto));
    }

    @Test
    void getItemIfItemIsNotExist() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemNotExistsException.class, () -> itemService.getItem(john.getId(), item.getId()));
    }

    @Test
    void getItemIfReceivedByOwner() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));
        Mockito.when(bookingRepository.findBookingsByItem(any())).thenReturn(List.of(booking));
        ItemDto secondItemDto = itemService.getItem(john.getId(), item.getId());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
        verify(bookingRepository, times(1)).findBookingsByItem(any());
        assertThat(secondItemDto, equalTo(secondItemDto));
    }

    @Test
    void getItemIfReceivedByNotOwner() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));
        ItemDto secondItemDto = itemService.getItem(2L, item.getId());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
        assertThat(secondItemDto, equalTo(secondItemDto));
    }

    @Test
    void getItemsByNotOwner() {
        Page<Item> page = new PageImpl<>(List.of(item));
        Mockito.when(itemRepository.findAll((Pageable) any())).thenReturn(page);
        List<ItemDto> itemDtoList = itemService.getItems(null, Pageable.ofSize(10));
        verify(itemRepository, times(1)).findAll((Pageable) any());
        assertThat(itemDtoList, equalTo(itemDtoList));
    }

    @Test
    void getItemsByOwner() {
        Page<Item> page = new PageImpl<>(List.of(item));
        Mockito.when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(page);
        List<ItemDto> itemDtoList = itemService.getItems(john.getId(), Pageable.ofSize(10));
        verify(itemRepository, times(1)).findByOwnerId(anyLong(), any());
        assertThat(itemDtoList, equalTo(itemDtoList));
    }

    @Test
    void searchItemIfTextIsBlank() {
        assertEquals(0, itemService.searchItem("", Pageable.ofSize(10)).size());
    }

    @Test
    void searchItem() {
        Page<Item> page = new PageImpl<>(List.of(item));
        Mockito.when(itemRepository.searchByText(anyString(), any())).thenReturn(page);
        List<ItemDto> itemDtoList = itemService.searchItem("item", Pageable.ofSize(10));
        verify(itemRepository, times(1)).searchByText(anyString(), any());
        assertThat(itemDtoList, equalTo(itemDtoList));
    }

    @Test
    void createCommentIfItemNotExist() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        assertThrows(ItemNotExistsException.class, () -> itemService.createComment(john.getId(), item.getId(), commentDto));
    }

    @Test
    void createCommentIfUserNotExist() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemService.createComment(john.getId(), item.getId(), commentDto));
    }

    @Test
    void createCommentIfStatusOfBookingIsRejected() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findBookingsCountByItemIdWhereStatusIsNotRejected(anyLong())).thenReturn(0);
        assertThrows(WrongBookingStatusException.class, () -> itemService.createComment(john.getId(), item.getId(), commentDto));
    }

    @Test
    void createCommentIfCommentIsBlank() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findBookingsCountByItemIdWhereStatusIsNotRejected(anyLong())).thenReturn(1);
        commentDto.setText("");
        assertThrows(WrongTextOfCommentException.class, () -> itemService.createComment(john.getId(), item.getId(), commentDto));
    }

    @Test
    void createCommentIfUserBookingAfterNextBooking() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findBookingsCountByItemIdWhereStatusIsNotRejected(anyLong())).thenReturn(1);
        Mockito.when(bookingRepository.findBookingsByItemIdAndStartIsAfterOrderByStartAsc(anyLong(), any()))
                .thenReturn(List.of(new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusHours(2), item, jane, Status.APPROVED)));
        assertThrows(IncorrectCreatorOfComment.class, () -> itemService.createComment(john.getId(), item.getId(), commentDto));
    }

    @Test
    void createComment() {
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findBookingsCountByItemIdWhereStatusIsNotRejected(anyLong())).thenReturn(1);
        Mockito.when(bookingRepository.findBookingsByItemIdAndStartIsAfterOrderByStartAsc(anyLong(), any()))
                .thenReturn(List.of(new Booking(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), item, john, Status.APPROVED)));
        Mockito.when(commentRepository.save(any())).thenReturn(comment);
        UserCommentDto secondCommentDto = itemService.createComment(john.getId(), item.getId(), commentDto);
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingsCountByItemIdWhereStatusIsNotRejected(anyLong());
        verify(bookingRepository, times(1)).findBookingsByItemIdAndStartIsAfterOrderByStartAsc(anyLong(), any());
        assertThat(secondCommentDto, equalTo(secondCommentDto));
    }

}
