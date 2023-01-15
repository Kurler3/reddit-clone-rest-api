package com.miguel.redditcloneapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubredditDto {
    private Long id;
    private String subredditName;
    private String description;
    private Integer numberOfPosts;
}
