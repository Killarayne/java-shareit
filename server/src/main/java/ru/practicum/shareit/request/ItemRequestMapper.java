package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    ItemRequest toItemRequestModel(ItemRequestDto itemRequestDto);

}
