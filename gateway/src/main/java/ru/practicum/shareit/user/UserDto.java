package ru.practicum.shareit.user;

import lombok.Getter;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Getter
public class UserDto {

    private long id;
    private String name;
    @NotBlank(groups = {Create.class})
    @Email(groups = {Update.class, Create.class})
    private String email;

}
