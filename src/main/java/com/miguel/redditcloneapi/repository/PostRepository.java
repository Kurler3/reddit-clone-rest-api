package com.miguel.redditcloneapi.repository;

import com.miguel.redditcloneapi.model.AppUser;
import com.miguel.redditcloneapi.model.Post;
import com.miguel.redditcloneapi.model.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findBySubreddit(Subreddit subreddit);

    List<Post> findByUser(AppUser user);

    Optional<Post> findByPostName(String postName);
}
