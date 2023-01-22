package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {

    UserDto toUserDto(User user);

    User toModel(UserDto userDto);

}
