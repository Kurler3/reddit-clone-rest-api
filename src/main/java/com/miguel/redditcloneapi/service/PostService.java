package com.miguel.redditcloneapi.service;


import com.miguel.redditcloneapi.dto.PostRequest;
import com.miguel.redditcloneapi.dto.PostResponse;
import com.miguel.redditcloneapi.exceptions.SpringRedditException;
import com.miguel.redditcloneapi.mappers.PostMapper;
import com.miguel.redditcloneapi.model.AppUser;
import com.miguel.redditcloneapi.model.Post;
import com.miguel.redditcloneapi.model.Subreddit;
import com.miguel.redditcloneapi.repository.PostRepository;
import com.miguel.redditcloneapi.repository.SubredditRepository;
import com.miguel.redditcloneapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private final PostMapper postMapper;
    private final SubredditRepository subredditRepository;

    private final AuthService authService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // SAVE
    @Transactional
    public PostResponse save(PostRequest postRequest) {

        // FIND SUBREDDIT
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName()).orElseThrow(() -> new SpringRedditException("Subreddit not found!"));

        // FIND USER
        AppUser user = authService.getCurrentUser();

        // CONVERT POST REQUEST -> POST
        Post post = postMapper.map(postRequest, subreddit, user);

        // SAVE
        postRepository.save(post);

        // RETURN PostResponse
        return postMapper.mapToDto(post);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<PostResponse> getAll() {
        // GET ALL POSTS
        List<Post> posts = postRepository.findAll();

        // MAP THEM TO LIST OF POST RESPONSE
        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public PostResponse getById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new SpringRedditException("Post not found"));

        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    // GET BY SUBREDDIT ID
    public List<PostResponse> getBySubredditId(Long subredditId) {

        Subreddit subreddit = subredditRepository.findById(subredditId).orElseThrow(() -> new SpringRedditException("Subreddit not found"));

        List<Post> posts = postRepository.findBySubreddit(subreddit);

        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }

    // GET BY USER ID
    @Transactional(readOnly = true)
    public List<PostResponse> getByUserId(Long userId) {
        AppUser appUser = userRepository.findById(userId).orElseThrow(() -> new SpringRedditException("User not found!"));

        List<Post> posts = postRepository.findByUser(appUser);

        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }
}
