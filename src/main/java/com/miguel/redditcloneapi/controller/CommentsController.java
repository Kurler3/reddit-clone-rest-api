package com.miguel.redditcloneapi.controller;

import com.miguel.redditcloneapi.dto.CommentDto;
import com.miguel.redditcloneapi.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
@Slf4j
public class CommentsController {

    private final CommentService commentService;

    // CREATE COMMENT
    @PostMapping("/create")
    public ResponseEntity<CommentDto> createComment(
            @RequestBody CommentDto commentDto
    ) {
        return new ResponseEntity<>(commentService.createComment(commentDto), HttpStatus.CREATED);
    }

    // GET ALL COMMENTS FOR POST
    @GetMapping("/by-postId/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(
            @PathVariable Long postId
    ) {
        return new ResponseEntity<>(commentService.getCommentsByPostId(postId), HttpStatus.OK);
    }

    // GET ALL COMMENTS BY USERNAME
    @GetMapping("/by-user/{username}")
    public ResponseEntity<List<CommentDto>> getCommentsByUsername(
            @PathVariable String username
    ) {
        return new ResponseEntity<>(commentService.getCommentsByUsername(username), HttpStatus.OK);
    }
}
