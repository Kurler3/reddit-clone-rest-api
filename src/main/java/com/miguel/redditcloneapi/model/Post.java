package com.miguel.redditcloneapi.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false
    )
    private String postName;

    @Nullable
    private String url;

    @Nullable
    @Lob
    private Integer description;

    private Integer voteCount;

    private Instant createdDate;

    // USER
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "appUser_id")
    private AppUser appUser;

    // SUB REDDIT
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subreddit_id")
    private Subreddit subreddit;

    // VOTES
    @OneToMany(fetch = LAZY, mappedBy = "post")
    private List<Vote> votes;

    // COMMENTS
    @OneToMany(fetch = LAZY, mappedBy = "post")
    private List<Comment> comments;
}
