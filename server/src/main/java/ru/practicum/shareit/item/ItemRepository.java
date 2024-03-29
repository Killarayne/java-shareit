package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByOwnerId(Long ownerId, Pageable pageable);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) and i.available = true" +
            " or upper(i.description) like upper(concat('%', ?1, '%') ) and i.available = true ")
    Page<Item> searchByText(String text, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);


}
