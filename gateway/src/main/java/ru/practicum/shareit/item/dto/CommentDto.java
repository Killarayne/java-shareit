package ru.practicum.shareit.item.dto;


import lombok.Getter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.User;

@Getter
public class CommentDto {

    private Long id;
    private String text;
    private User author;
    private Item item;

}


