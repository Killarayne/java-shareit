package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserCommentDto {
    private Long id;

    private String text;

    private String authorName;


    private LocalDateTime created;

    public UserCommentDto(Long id, String text, String authorName) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;
        this.created = LocalDateTime.now();
    }
}
