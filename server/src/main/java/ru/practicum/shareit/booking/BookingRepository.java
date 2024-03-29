package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByBookerId(Long userId, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN Item AS i ON b.item.id = i.id " +
            "WHERE i.ownerId = :userId")
    List<Booking> findBookingsByOwnerId(@Param("userId") Long userId, Pageable pageable);

    List<Booking> findBookingsByItem(Item item);

    @Query(value = "SELECT COUNT (b) FROM Booking b WHERE b.status <> 'REJECTED' AND b.item.id = :itemId")
    Integer findBookingsCountByItemIdWhereStatusIsNotRejected(@Param("itemId") Long itemId);

    List<Booking> findBookingsByItemIdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime time);

    Page<Booking> findAllByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId, Pageable page);

}
