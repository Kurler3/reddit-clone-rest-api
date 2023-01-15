package com.miguel.redditcloneapi.model;

import com.miguel.redditcloneapi.exceptions.SpringRedditException;

import java.util.Arrays;

public enum VoteType {

    UPVOTE(1), DOWNVOTE(-1);

    private int direction;
    public Integer getDirection() {return this.direction;}
    VoteType(int direction) {
    }

    // LOOK UP
    public static VoteType lookup(Integer direction) {
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getDirection().equals(direction))
                .findAny()
                .orElseThrow(() -> new SpringRedditException("Vote not found"));
    }
}
