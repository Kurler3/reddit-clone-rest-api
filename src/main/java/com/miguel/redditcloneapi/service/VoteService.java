package com.miguel.redditcloneapi.service;

import com.miguel.redditcloneapi.dto.VoteDto;
import com.miguel.redditcloneapi.exceptions.SpringRedditException;
import com.miguel.redditcloneapi.model.Post;
import com.miguel.redditcloneapi.model.Vote;
import com.miguel.redditcloneapi.model.VoteType;
import com.miguel.redditcloneapi.repository.PostRepository;
import com.miguel.redditcloneapi.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VoteService {

    private final PostRepository postRepository;
    private final VoteRepository voteRepository;

    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        // GET POST
        Post post = postRepository.findById(voteDto.getPostId()).orElseThrow(() -> new SpringRedditException("Post not found!"));
        // FIND VOTE BY POST AND USER
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndAppUserOrderByVoteIdDesc(post, authService.getCurrentUser());
        // IF VOTE IS PRESENT AND IS THE SAME TYPE, THEN THROW ERROR
        if(voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
            throw new SpringRedditException("You have already " + voteDto.getVoteType() + "'d for this post");
        }
        Integer newVoteCount = VoteType.UPVOTE.equals(voteDto.getVoteType()) ? post.getVoteCount() + 1 : post.getVoteCount() - 1;
        post.setVoteCount(newVoteCount);

        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .appUser(authService.getCurrentUser())
                .build();
    }
}
