package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserBookingDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Status status;

    private User booker;

    private Item item;


}
