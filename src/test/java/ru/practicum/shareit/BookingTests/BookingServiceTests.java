package ru.practicum.shareit.BookingTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UserBookingDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {BookingServiceImpl.class, BookingMapperImpl.class})
public class BookingServiceTests {
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private ItemRepository itemRepository;
    @Autowired
    private BookingMapper bookingMapper;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private BookingSerivce bookingSerivce;
    private BookingDto bookingDto;
    private Booking booking;
    private User john;
    private User jane;
    private Item item;

    @BeforeEach
    void beforeEach() {
        john = new User(1L, "John", "john@doe.com");
        jane = new User(2L, "Jane", "jane@doe.com");
        item = new Item(1, "item", "description of item", true, 1L, 1L);
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), Status.REJECTED, 1L);
        booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), item, john, Status.WAITING);
    }

    @Test
    void createBookingIfUserNotExist() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> bookingSerivce.createBooking(john.getId(), bookingDto));
    }

    @Test
    void createBookingIfItemNotExist() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemNotExistsException.class, () -> bookingSerivce.createBooking(john.getId(), bookingDto));
    }

    @Test
    void createBookingByItemOwner() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(UserNotFoundException.class, () -> bookingSerivce.createBooking(john.getId(), bookingDto));
    }

    @Test
    void createBookingIfItemIsUnavailable() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(jane));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        item.setAvailable(false);
        assertThrows(NotAvailableItemException.class, () -> bookingSerivce.createBooking(jane.getId(), bookingDto));
    }

    @Test
    void createBookingIfEndBeforeStart() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(jane));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        bookingDto.setEnd(LocalDateTime.now().minusHours(1));
        assertThrows(WrongTimeException.class, () -> bookingSerivce.createBooking(jane.getId(), bookingDto));
    }

    @Test
    void createBooking() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(jane));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);
        UserBookingDto userBookingDto = bookingSerivce.createBooking(jane.getId(), bookingDto);
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any());
        assertThat(userBookingDto, equalTo(userBookingDto));
    }

    @Test
    void approveIfBookingNotFound() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BookingNotExistException.class, () -> bookingSerivce.approve(john.getId(), booking.getId(), true));
    }

    @Test
    void approveIfStatusApproved() {
        booking.setStatus(Status.APPROVED);
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(WrongBookingStatusException.class, () -> bookingSerivce.approve(john.getId(), booking.getId(), true));
    }

    @Test
    void approveIfNotOwnerOfItem() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(UserNotFoundException.class, () -> bookingSerivce.approve(jane.getId(), booking.getId(), true));
    }

    @Test
    void approveIfApprovedByOwner() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);
        UserBookingDto userBookingDto = bookingSerivce.approve(john.getId(), booking.getId(), true);
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any());
        assertThat(userBookingDto, equalTo(userBookingDto));
    }

    @Test
    void approveIfRejectedByOwner() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);
        UserBookingDto userBookingDto = bookingSerivce.approve(john.getId(), booking.getId(), false);
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any());
        assertThat(userBookingDto, equalTo(userBookingDto));
    }

    @Test
    void getBookingIfBookingIsNotExist() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BookingNotExistException.class, () -> bookingSerivce.getBooking(jane.getId(), booking.getId()));
    }

    @Test
    void getBookingIfNotBookerOrOwner() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(UserNotFoundException.class, () -> bookingSerivce.getBooking(3L, booking.getId()));
    }

    @Test
    void getBooking() {
        Mockito.when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        UserBookingDto userBookingDto = bookingSerivce.getBooking(john.getId(), booking.getId());
        verify(bookingRepository, times(1)).findById(anyLong());
        assertThat(userBookingDto, equalTo(userBookingDto));
    }

    @Test
    void getBookingsByOwnerIfUserIsNotExist() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> bookingSerivce.getBookingsByOwner(jane.getId(), State.ALL, Pageable.ofSize(10)));
    }

    @Test
    void getBookingsByOwnerWhenStateIsAll() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any())).thenReturn(new PageImpl<>(List.of(booking)));
        List<UserBookingDto> userBookingDtoList = bookingSerivce.getBookingsByOwner(john.getId(), State.ALL, Pageable.ofSize(10));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
        assertThat(userBookingDtoList, equalTo(userBookingDtoList));
    }

    @Test
    void getBookingsByOwnerWhenStateIsCurrent() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findBookingsByOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        List<UserBookingDto> userBookingDtoList = bookingSerivce.getBookingsByOwner(john.getId(), State.CURRENT, Pageable.ofSize(10));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingsByOwnerId(anyLong(), any());
        assertThat(userBookingDtoList, equalTo(userBookingDtoList));
    }

    @Test
    void getBookingsByOwnerWhenStateIsFuture() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findBookingsByOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        List<UserBookingDto> userBookingDtoList = bookingSerivce.getBookingsByOwner(john.getId(), State.FUTURE, Pageable.ofSize(10));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingsByOwnerId(anyLong(), any());
        assertThat(userBookingDtoList, equalTo(userBookingDtoList));
    }

    @Test
    void getBookingsByOwnerWhenStateIsPast() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findBookingsByOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        List<UserBookingDto> userBookingDtoList = bookingSerivce.getBookingsByOwner(john.getId(), State.PAST, Pageable.ofSize(10));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingsByOwnerId(anyLong(), any());
        assertThat(userBookingDtoList, equalTo(userBookingDtoList));
    }

    @Test
    void getBookingsByOwnerWhenStateIsWaiting() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findBookingsByOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        List<UserBookingDto> userBookingDtoList = bookingSerivce.getBookingsByOwner(john.getId(), State.WAITING, Pageable.ofSize(10));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingsByOwnerId(anyLong(), any());
        assertThat(userBookingDtoList, equalTo(userBookingDtoList));
    }

    @Test
    void getBookingsByOwnerWhenStateIsRejected() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findBookingsByOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        List<UserBookingDto> userBookingDtoList = bookingSerivce.getBookingsByOwner(john.getId(), State.REJECTED, Pageable.ofSize(10));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingsByOwnerId(anyLong(), any());
        assertThat(userBookingDtoList, equalTo(userBookingDtoList));
    }

    @Test
    void getBookingsByBookerIfUserIsNotExist() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> bookingSerivce.getBookingsByBooker(jane.getId(), State.ALL, Pageable.ofSize(10)));
    }

    @Test
    void getBookingsByBookerWhenStateIsAll() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(new PageImpl<>(List.of(booking)));
        List<UserBookingDto> userBookingDtoList = bookingSerivce.getBookingsByBooker(john.getId(), State.ALL, Pageable.ofSize(10));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(anyLong(), any());
        assertThat(userBookingDtoList, equalTo(userBookingDtoList));
    }

    @Test
    void getBookingsByBookerWhenStateIsFuture() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(john));
        Mockito.when(bookingRepository.findBookingsByBookerId(anyLong(), any())).thenReturn(List.of(booking));
        List<UserBookingDto> userBookingDtoList = bookingSerivce.getBookingsByBooker(john.getId(), State.FUTURE, Pageable.ofSize(10));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findBookingsByBookerId(anyLong(), any());
        assertThat(userBookingDtoList, equalTo(userBookingDtoList));
    }

}
