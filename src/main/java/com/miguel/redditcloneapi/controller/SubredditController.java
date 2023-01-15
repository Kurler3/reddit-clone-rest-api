package com.miguel.redditcloneapi.controller;

import com.miguel.redditcloneapi.dto.SubredditDto;
import com.miguel.redditcloneapi.service.SubredditService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subreddit")
@AllArgsConstructor
@Slf4j
public class SubredditController {

    private final SubredditService subredditService;

    // CREATE SUB REDDIT
    @PostMapping("/create")
    public ResponseEntity<SubredditDto> createSubreddit(
            @RequestBody SubredditDto subredditDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subredditService.save(subredditDto));
    }

    // GET ALL SUBREDDITS
    @GetMapping("/all")
    public ResponseEntity<List<SubredditDto>> getAllSubreddits() {
        List<SubredditDto> allSubreddits = subredditService.getAll();

        return new ResponseEntity<>(allSubreddits, HttpStatus.OK);
    }

    // GET SUBREDDIT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<SubredditDto> getSubreddit(
            @PathVariable Long id
    ) {
        return new ResponseEntity<>(subredditService.getById(id), HttpStatus.FOUND);
    }
 }
