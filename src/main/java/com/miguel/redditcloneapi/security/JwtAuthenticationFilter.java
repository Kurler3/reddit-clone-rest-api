package com.miguel.redditcloneapi.security;

import com.miguel.redditcloneapi.service.UserDetailsServiceImplementation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImplementation userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // CHECK IF IN AUTH
        if(request.getServletPath().contains("/api/auth/**")) {
            filterChain.doFilter(request, response);
        }
        else {
            // GET JWT FROM REQUEST HEADER
            String jwt = getJwtFromRequest(request);

            // IF NOT VALID, THEN CONTINUE FILTER CHAIN
            if(!jwtProvider.evaluateToken(jwt)) {
                filterChain.doFilter(request, response);
            }

            // GET USERNAME
            String username = jwtProvider.getUsernameFromJwt(jwt);

            // IF NULL, THEN CONTINUE FILTER CHAIN
            if(username == null) filterChain.doFilter(request, response);

            // GET USER DETAILS -> TO BE ABLE TO GET THE AUTHORITIES
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // CREATE AUTHENTICATION TOKEN
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    userDetails.getAuthorities()
            );

            // SET WEB AUTHENTICATION DETAILS
            // THIS MAKES SECURITY EVEN MORE ROBUST, SINCE SPRING WILL NOW KNOW WHAT THE IP ADDRESS OF THE DEVICE MAKING THE REQUEST IS
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // SET THE AUTH IN THE SECURITY CONTEXT HOLDER
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // CONTINUE THE FILTER
            filterChain.doFilter(request, response);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);

        // CHECK IF IS NOT NULL AND CONTAINS BEARER
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring("Bearer ".length());
        }

        // ELSE RETURN BEARER TOKEN
        return bearerToken;
    }
}
