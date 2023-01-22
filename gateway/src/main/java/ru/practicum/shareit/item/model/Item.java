package ru.practicum.shareit.item.model;

import lombok.Getter;


@Getter
public class Item {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private Long requestId;

}
