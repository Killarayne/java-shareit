package ru.practicum.shareit.BookingTests;

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
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.UserBookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class, BookingMapperImpl.class})
public class BookingControllerTests {

    @MockBean
    private BookingSerivce bookingSerivce;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private BookingMapper bookingMapper;
    private BookingDto bookingDto;
    private Booking booking;
    private UserBookingDto userBookingDto;


    @BeforeEach
    void beforeEach() {
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3), Status.REJECTED, 1L);
        booking = bookingMapper.toBookingModel(bookingDto);
        userBookingDto = bookingMapper.toUserBookingDto(booking);
    }

    @Test
    @SneakyThrows
    void createBooking() {
        Mockito.when(bookingSerivce.createBooking(anyLong(), any())).thenReturn(userBookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    @SneakyThrows
    void approved() {
        Mockito.when(bookingSerivce.approve(anyLong(), anyLong(), any())).thenReturn(userBookingDto);
        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    @SneakyThrows
    void getBooking() {
        Mockito.when(bookingSerivce.getBooking(anyLong(), anyLong())).thenReturn(userBookingDto);
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    @SneakyThrows
    void getBookingsByOwner() {
        Mockito.when(bookingSerivce.getBookingsByOwner(anyLong(), any(), any())).thenReturn(List.of(userBookingDto));
        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(1)))
                .andExpect(jsonPath("$[0]['id']", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0]['status']", is(bookingDto.getStatus().toString())));
    }

    @Test
    @SneakyThrows
    void getBookingsByBooker() {
        Mockito.when(bookingSerivce.getBookingsByBooker(anyLong(), any(), any())).thenReturn(List.of(userBookingDto));
        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(1)))
                .andExpect(jsonPath("$[0]['id']", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0]['status']", is(bookingDto.getStatus().toString())));
    }


}
