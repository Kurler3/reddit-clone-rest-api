package com.miguel.redditcloneapi.security;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class JwtProvider {

    private final long validityInMilliseconds = 3600000 * 24; // 24h
    private final String secretKey = "very_secret_key";
    public String createToken(Authentication authentication) {

        User user = (User)authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // EVALUATE TOKEN
    public boolean evaluateToken(String jwt) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJwt(jwt).getBody();
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // GET USERNAME
    public String getUsernameFromJwt(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(jwt).getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}