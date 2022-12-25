package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.UserCommentDto;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "author.name", target = "authorName")
    UserCommentDto toUserCommentDto(Comment comment);

    Comment toCommentModel(CommentDto commentDto);
}
