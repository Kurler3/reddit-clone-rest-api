package com.miguel.redditcloneapi.repository;

import com.miguel.redditcloneapi.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    public Optional<VerificationToken> findByToken(String token);
}
