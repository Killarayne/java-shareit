package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByBooker_Id(Long userId);

    @Query("SELECT new Booking(b.id,b.start,b.end,b.item, b.booker, b.status)" +
            "FROM Item AS i " +
            "LEFT JOIN Booking AS b ON b.item.id = i.id " +
            "LEFT JOIN User AS u ON b.booker.id = u.id " +
            "WHERE i.ownerId = ?1")
    List<Booking> findBookingsByOwner_Id(Long userId);

    List<Booking> findBookingsByItem(Item item);


}
