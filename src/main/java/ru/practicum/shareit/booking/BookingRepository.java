package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByBooker_Id(Long userId);

    @Query("SELECT new Booking(b.id,b.start,b.end,b.item, b.booker, b.status)" +
            "FROM Item AS i " +
            "JOIN Booking AS b ON b.item.id = i.id " +
            "JOIN User AS u ON b.booker.id = u.id " +
            "WHERE i.ownerId = ?1")
    List<Booking> findBookingsByOwner_Id(Long userId);

    List<Booking> findBookingsByItem(Item item);

    @Query(value = "SELECT COUNT (b) FROM Booking b WHERE b.status <> 'REJECTED' AND b.item.id = ?1")
    Integer findBookingsCountByItemIdWhereStatusIsNotRejected(Long itemId);

    List<Booking> findBookingsByItemIdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime time);

}
