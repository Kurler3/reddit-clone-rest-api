package com.miguel.redditcloneapi.service;

import com.miguel.redditcloneapi.dto.SubredditDto;
import com.miguel.redditcloneapi.exceptions.SpringRedditException;
import com.miguel.redditcloneapi.mappers.SubredditMapper;
import com.miguel.redditcloneapi.model.Subreddit;
import com.miguel.redditcloneapi.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit savedSubreddit = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));

        subredditDto.setId(savedSubreddit.getId());

        return subredditDto;
    }

    // GET ALL
    // ANNOTATE WITH TRANSACTIONAL BECAUSE WE ARE INTERACTING WITH DB, readonly as TRUE BECAUSE WE ARE JUST QUERYING IT.
    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        // GET ALL (LIST) -> STREAM -> MAP TO SUB REDDIT DTO -> COLLECT AS LIST
        return subredditRepository.findAll().stream().map(subredditMapper::mapSubredditToDto).collect(Collectors.toList());
    }

    public SubredditDto getById(Long id) {

        Subreddit subreddit = subredditRepository.findById(id).orElseThrow(() -> new SpringRedditException("Subreddit not found!"));

        return subredditMapper.mapSubredditToDto(subreddit);
    }
}
