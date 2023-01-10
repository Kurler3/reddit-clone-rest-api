package com.miguel.redditcloneapi.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    private Instant createdDate;
    private boolean enabled;

    // POSTS RELATIONSHIP
    @OneToMany(fetch = LAZY, mappedBy = "appUser")
    private List<Post> posts;

    // SUBREDDIT RELATIONSHIP
    @OneToMany(fetch = LAZY, mappedBy = "appUser")
    private List<Subreddit> subreddits;

    // VOTES
    @OneToMany(fetch = LAZY, mappedBy = "appUser")
    private List<Vote> votes;

    // COMMENTS
    @OneToMany(fetch = LAZY, mappedBy = "appUser")
    private List<Comment> comments;
}
