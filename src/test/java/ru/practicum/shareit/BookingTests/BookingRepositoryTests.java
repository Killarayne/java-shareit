package ru.practicum.shareit.BookingTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
public class BookingRepositoryTests {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User john;
    private Item item;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        john = new User(1L, "John", "john@doe.com");
        item = new Item(1, "item", "description", true, john.getId(), null);
        booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12), item, john, Status.WAITING);
        userRepository.save(john);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    void findBookingsCountByItemIdWhereStatusIsNotRejected() {
        Integer bookingsCountByItemIdWhereStatusIsNotRejected = bookingRepository.findBookingsCountByItemIdWhereStatusIsNotRejected(item.getId());
        assertEquals(1, bookingsCountByItemIdWhereStatusIsNotRejected);
    }

    @Test
    void findBookingsByOwnerId() {
        List<Booking> bookingsByOwnerId = bookingRepository.findBookingsByOwnerId(item.getOwnerId(), Pageable.unpaged());
        assertEquals(booking.getId(), bookingsByOwnerId.get(0).getId());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

}
