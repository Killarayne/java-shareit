package ru.practicum.shareit.ItemRequestTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTests {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @BeforeEach
    void beforeEach() {
        itemRequest1 = new ItemRequest();
        itemRequest2 = new ItemRequest();
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
    }

    @Test
    void getItemRequests() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAll();
        assertEquals(2, itemRequests.size());
    }

}
