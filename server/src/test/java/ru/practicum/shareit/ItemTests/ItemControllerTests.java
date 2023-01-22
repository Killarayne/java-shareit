package ru.practicum.shareit.ItemTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ServerItemController.class, ItemMapperImpl.class, UserMapperImpl.class, CommentMapperImpl.class})

public class ItemControllerTests {
    private final List<Item> devices = new ArrayList<>();
    private final List<ItemDto> devicesDto = new ArrayList<>();
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private ItemRepository itemRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    private User john;
    private User jane;
    private Item hammer;
    private ItemDto hammerDto;
    private Item bulldozer;
    private ItemDto bulldozerDto;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        john = new User(1L, "John", "john@doe.com");
        jane = new User(2L, "Jane", "jane@doe.com");
        hammer = new Item(1L, "Hammer", "Hand hammer", true, john.getId(), null);
        hammerDto = itemMapper.toItemDto(hammer);
        bulldozer = new Item(2L, "Bulldozer", "Big machine", true, jane.getId(), null);
        bulldozerDto = itemMapper.toItemDto(bulldozer);
        devices.addAll(List.of(hammer, bulldozer));
        devicesDto.addAll(List.of(hammerDto, bulldozerDto));
        comment = new Comment(1L, "First comment", jane, hammer);
        commentDto = commentMapper.toCommentDto(comment);
    }

    @Test
    @SneakyThrows
    void create() {
        hammerDto.setAvailable(null);
        Mockito.when(itemService.createItem(any())).thenReturn(itemMapper.toItemDto(hammer));
        Mockito.when(userService.getUser(john.getId())).thenReturn(userMapper.toUserDto(john));
        Mockito.when(userService.getUser(jane.getId())).thenReturn(userMapper.toUserDto(jane));
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(hammerDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(hammer.getName())));
    }

    @Test
    @SneakyThrows
    void getOne() {
        Mockito.when(itemService.getItem(any(), any())).thenReturn(hammerDto);
        mvc.perform(get("/items/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(hammer.getName())));
    }

    @Test
    @SneakyThrows
    void update() {
        ItemDto hammerPartial = new ItemDto();
        hammerPartial.setName(hammer.getName());
        Mockito.when(itemService.updateItem(anyLong(), any())).thenReturn(hammerDto);
        mvc.perform(put("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(hammerPartial))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(hammer.getName())));
    }

    @Test
    @SneakyThrows
    void getAll() {
        Mockito.when(itemService.getItems(anyLong(), any())).thenReturn(devicesDto);
        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[0]['id']", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$[1]['name']", is(bulldozer.getName())));
    }

    @Test
    @SneakyThrows
    void search() {
        Mockito.when(itemService.searchItem(any(), any())).thenReturn(devicesDto);
        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .queryParam("text", "anytext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[0]['id']", is(hammer.getId()), Long.class))
                .andExpect(jsonPath("$[1]['name']", is(bulldozer.getName())));
    }

    @Test
    @SneakyThrows
    void addComment() {
        Mockito.when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(commentMapper.toUserCommentDto(comment));
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['id']", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$['text']", is(comment.getText())));
    }

}
