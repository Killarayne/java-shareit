package ru.practicum.shareit.ItemRequestTests;

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
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestMapperImpl;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.ServerItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ServerItemRequestController.class, ItemRequestMapperImpl.class})
public class ItemRequestControllerTests {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemRequestMapper itemRequestMapper;
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto itemRequestDtoTwo;

    @BeforeEach
    void beforeEach() {
        itemRequestDto = new ItemRequestDto(1, "test description", LocalDateTime.now(), null);
        itemRequestDtoTwo = new ItemRequestDto(2, "test descriptionTwo", LocalDateTime.now(), null);
    }

    @Test
    @SneakyThrows
    void createRequest() {
        Mockito.when(itemRequestService.createRequest(any(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    @SneakyThrows
    void getItemRequest() {
        Mockito.when(itemRequestService.getRequest(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    @SneakyThrows
    void getRequestsByRequestor() {
        Mockito.when(itemRequestService.getRequestsByRequestor(anyLong())).thenReturn(List.of(itemRequestDto, itemRequestDtoTwo));
        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[0]['id']", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[1]['description']", is(itemRequestDtoTwo.getDescription())));
    }

    @Test
    @SneakyThrows
    void getRequestsAll() {
        Mockito.when(itemRequestService.getRequestsAll(any(), anyLong())).thenReturn(List.of(itemRequestDto, itemRequestDtoTwo));
        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[0]['id']", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[1]['description']", is(itemRequestDtoTwo.getDescription())));
    }

}
