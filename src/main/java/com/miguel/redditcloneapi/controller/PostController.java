package com.miguel.redditcloneapi.controller;

import com.miguel.redditcloneapi.dto.PostRequest;
import com.miguel.redditcloneapi.dto.PostResponse;
import com.miguel.redditcloneapi.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
@Slf4j
public class PostController {

    final private PostService postService;

    // CREATE POST
    @PostMapping("/create")
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest postRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.save(postRequest));
    }

    // GET ALL POSTS
    @GetMapping("/all")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return new ResponseEntity<>(postService.getAll(), HttpStatus.OK);
    }

    // GET POST BY ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> findById(
            @PathVariable Long id
    ) {
        return new ResponseEntity<PostResponse>(postService.getById(id), HttpStatus.FOUND);
    }

    // GET POSTS BY SUBREDDIT ID
    @GetMapping("/subreddit/{subredditId}")
    public ResponseEntity<List<PostResponse>> findBySubreddit(
            @PathVariable Long subredditId
    ) {
        return new ResponseEntity<>(postService.getBySubredditId(subredditId), HttpStatus.OK);
    }

    // GET POSTS BY USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> findByUserId(
            @PathVariable Long userId
    ) {
        return new ResponseEntity<>(postService.getByUserId(userId), HttpStatus.OK);
    }


}
