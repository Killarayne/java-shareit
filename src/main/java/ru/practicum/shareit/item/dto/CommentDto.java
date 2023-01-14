package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private String text;
    private User author;
    private Item item;

}


