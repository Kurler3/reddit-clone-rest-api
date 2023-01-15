package com.miguel.redditcloneapi.repository;

import com.miguel.redditcloneapi.model.AppUser;
import com.miguel.redditcloneapi.model.Post;
import com.miguel.redditcloneapi.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    // FIND A VOTE BY A USER AND IN A POST BY THE DESCENDING VOTE ID ORDER
    Optional<Vote> findTopByPostAndAppUserOrderByVoteIdDesc(Post post, AppUser appUser);
}
