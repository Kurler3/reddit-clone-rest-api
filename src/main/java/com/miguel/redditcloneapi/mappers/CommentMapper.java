package com.miguel.redditcloneapi.mappers;


import com.miguel.redditcloneapi.dto.CommentDto;
import com.miguel.redditcloneapi.model.AppUser;
import com.miguel.redditcloneapi.model.Comment;
import com.miguel.redditcloneapi.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    // FROM COMMENT DTO TO COMMENT
    @Mapping(target="createdDate", expression = "java(java.time.Instant.now())")
    Comment dtoToComment(CommentDto commentsDto, Post post, AppUser appUser);

    // FROM COMMENT TO COMMENT DTO
    @Mapping(target = "postName", source="post.postName")
    @Mapping(target = "userName", source="appUser.username")
    CommentDto commentToDto(Comment comment);
}
