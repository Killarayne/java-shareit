package ru.practicum.shareit.ItemTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
public class ItemRepositoryTests {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item1;
    private Item item2;
    private User john;

    @BeforeEach
    void beforeEach() {
        john = new User(1L, "John", "john@doe.com");
        item1 = new Item(1L, "item1", "description1", true, john.getId(), 1L);
        item2 = new Item(2L, "item2", "description2", true, john.getId(), 2L);
        userRepository.save(john);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void getItems() {
        List<Item> items = itemRepository.findAll();
        assertEquals(2, items.size());
    }

    @Test
    void searchByText() {
        Page<Item> page = itemRepository.searchByText("description2", Pageable.unpaged());
        assertEquals(item2.getId(), page.toList().get(0).getId());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

}
