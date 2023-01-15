package com.miguel.redditcloneapi.service;


import com.miguel.redditcloneapi.dto.AuthenticationResponse;
import com.miguel.redditcloneapi.dto.LoginRequest;
import com.miguel.redditcloneapi.dto.RegisterRequest;
import com.miguel.redditcloneapi.exceptions.SpringRedditException;
import com.miguel.redditcloneapi.model.AppUser;
import com.miguel.redditcloneapi.model.NotificationEmail;
import com.miguel.redditcloneapi.model.VerificationToken;
import com.miguel.redditcloneapi.repository.UserRepository;
import com.miguel.redditcloneapi.repository.VerificationTokenRepository;
import com.miguel.redditcloneapi.security.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    @Transactional
    public void signUp(RegisterRequest registerRequest) {
        AppUser appUser = new AppUser();

        if(
                registerRequest.getEmail() == null ||
                        registerRequest.getUsername() == null ||
                        registerRequest.getPassword() == null
        ) {
            throw new SpringRedditException("Data incomplete for signing up");
        }

        // CHECK IF USER ALREADY EXISTS WITH THAT EMAIL/USERNAME, IF EXISTS, THEN THROW ERROR
        if(
                userRepository.findAppUserByEmail(registerRequest.getEmail()).isPresent() ||
                        userRepository.findByUsername(registerRequest.getUsername()).isPresent()
        ) {
            throw new SpringRedditException("User already exists");
        }

        // MAP THE FIELDS TO THE NEW USER
        appUser.setEmail(registerRequest.getEmail());
        appUser.setUsername(registerRequest.getUsername());
        // PASSWORD NEEDS TO BE ENCODED
        appUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        appUser.setCreatedDate(Instant.now());
        // BY DEFAULT, SET IT AS DISABLED (NEED EMAIL VERIFICATION)
        appUser.setEnabled(false);

        // SAVE NEW USER
        userRepository.save(appUser);

        // GENERATE VERIFICATION TOKEN
        String token = generateVerificationToken(appUser);

        NotificationEmail notificationEmail = new NotificationEmail(
                "Activate your account",
                appUser.getEmail(),
                "Thank you for signing up to Spring Reddit, " +
                        "please click on the below url to activate your account: " +
                        "http://localhost:8080/api/auth/accountVerification/" + token
        );
        // SEND VERIFICATION EMAIL
        mailService.sendMail(notificationEmail);
    }

    private String generateVerificationToken(AppUser user) {
        // TOKEN THAT WILL BE SENT TO THE ACCOUNT VERIFICATION EMAIL
        String token = UUID.randomUUID().toString();

        // PERSIST TOKEN IN THE DB
        VerificationToken verificationToken =  new VerificationToken();

        // SET TOKEN
        verificationToken.setToken(token);
        // SET USER
        verificationToken.setAppUser(user);
        // SET EXPIRATION DATE
        //verificationToken.setExpirationDate();

        verificationTokenRepository.save(verificationToken);

        return token;
    }

    @Transactional
    public void verifyAccount(String token) {

        // FIND VERIFICATION TOKEN OBJECT IN DB
        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);

        if(verificationTokenOptional.isEmpty()) {
            throw new SpringRedditException("Token not found!");
        }

        VerificationToken verificationToken = verificationTokenOptional.get();

        // IF THE EXPIRATION DATE IS PASSED
        if(verificationToken.getExpirationDate() != null && verificationToken.getExpirationDate().isBefore(Instant.now())) {
            throw new SpringRedditException("Token expired!");
        }

        // GET USER USERNAME
        String username = verificationToken.getAppUser().getUsername();

        // FIND BY USERNAME
        AppUser appUser = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found :/"));

        // SET ENABLED
        appUser.setEnabled(true);

        // SAVE
        userRepository.save(appUser);

        // DELETE TOKEN FROM DB
        verificationTokenRepository.deleteById(verificationToken.getId());
    }

    @Transactional(readOnly = true)
    public AppUser getCurrentUser() {
        Claims principal = (Claims) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getSubject()));
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        // AUTHENTICATE
        // THE AUTHENTICATION MANAGER WILL TAKE CARE OF CHECKING IF THE USER EXISTS IN DB AND IF THE PASSWORD MATCHES.
        // THIS WILL ONLY HAPPEN, BECAUSE WE SET THE USER DETAILS SERVICE IMPLEMENTATION AND THE PASSWORD ENCODER ON THE AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()
                )
        );

        // SET AUTH IN THE SECURITY CONTEXT HOLDER
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // CREATE JWT
        String jwtToken = jwtProvider.createToken(authentication);

        // RETURN OBJECT WITH JWT + USERNAME
        return new AuthenticationResponse(
                jwtToken,
                loginRequest.getUsername()
        );
    }
}
