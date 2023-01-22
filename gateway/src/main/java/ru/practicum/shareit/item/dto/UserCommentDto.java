package ru.practicum.shareit.item.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCommentDto {

    private final Long id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created;

    public UserCommentDto(Long id, String text, String authorName) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;
        this.created = LocalDateTime.now();
    }

}
