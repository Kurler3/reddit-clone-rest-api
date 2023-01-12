package com.miguel.redditcloneapi.repository;

import com.miguel.redditcloneapi.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
}
