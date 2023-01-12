package com.miguel.redditcloneapi.repository;

import com.miguel.redditcloneapi.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findAppUserByEmail(String email);

    Optional<AppUser> findByUsername(String username);
}
