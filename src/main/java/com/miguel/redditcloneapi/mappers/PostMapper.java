package com.miguel.redditcloneapi.mappers;

import com.miguel.redditcloneapi.dto.PostRequest;
import com.miguel.redditcloneapi.dto.PostResponse;
import com.miguel.redditcloneapi.model.AppUser;
import com.miguel.redditcloneapi.model.Post;
import com.miguel.redditcloneapi.model.Subreddit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    // MAP FROM REQUEST TO POST
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    Post map(PostRequest postRequest, Subreddit subreddit, AppUser user);

    // FROM POST TO POST RESPONSE
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    PostResponse mapToDto(Post post);
}
