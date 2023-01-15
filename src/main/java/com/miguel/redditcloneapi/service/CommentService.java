package com.miguel.redditcloneapi.service;

import com.miguel.redditcloneapi.dto.CommentDto;
import com.miguel.redditcloneapi.exceptions.SpringRedditException;
import com.miguel.redditcloneapi.mappers.CommentMapper;
import com.miguel.redditcloneapi.model.AppUser;
import com.miguel.redditcloneapi.model.Comment;
import com.miguel.redditcloneapi.model.Post;
import com.miguel.redditcloneapi.repository.CommentRepository;
import com.miguel.redditcloneapi.repository.PostRepository;
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
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    // CREATE COMMENT
    @Transactional
    public CommentDto createComment(CommentDto commentDto) {

        // GET POST
        Post post = postRepository.findByPostName(commentDto.getPostName())
                .orElseThrow(() -> new SpringRedditException("Post not found!"));

        // GET APP USER
        AppUser appUser = authService.getCurrentUser();

        // CONVERT TO COMMENT
        Comment comment = commentMapper.dtoToComment(commentDto, post, appUser);

        // SAVE COMMENT IN DB
        commentRepository.save(comment);

        // SET NEW ID IN THE COMMENT DTO
        commentDto.setId(comment.getId());

        // RETURN COMMENT DTO
        return commentDto;
    }

    // GET ALL COMMENTS GIVEN POST ID
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        // FETCH POST
        Post post = postRepository.findById(postId).orElseThrow(() -> new SpringRedditException("Post not found"));

        // FIND COMMENTS BY POST
        List<Comment> comments = commentRepository.findByPost(post);

        return commentsListToDtoList(comments);
    }

    // GET ALL COMMENTS GIVEN USERNAME
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByUsername(String username) {

        // FETCH USER
        AppUser appUser = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found!"));

        // FIND LIST OF COMMENTS CREATED BY THIS USER
        List<Comment> comments = commentRepository.findByUser(appUser);

        // RETURN
        return commentsListToDtoList(comments);
    }

    // FROM LIST OF COMMENTS -> LIST OF COMMENTS DTO
     private List<CommentDto> commentsListToDtoList(List<Comment> comments) {
        return comments.stream().map(commentMapper::commentToDto).collect(Collectors.toList());
     }
}
