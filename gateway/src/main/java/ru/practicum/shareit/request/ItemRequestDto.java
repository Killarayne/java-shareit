package ru.practicum.shareit.request;

import lombok.Getter;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
