package com.miguel.redditcloneapi.service;


import com.miguel.redditcloneapi.dto.RegisterRequest;
import com.miguel.redditcloneapi.exceptions.SpringRedditException;
import com.miguel.redditcloneapi.model.AppUser;
import com.miguel.redditcloneapi.model.NotificationEmail;
import com.miguel.redditcloneapi.model.VerificationToken;
import com.miguel.redditcloneapi.repository.UserRepository;
import com.miguel.redditcloneapi.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public void signUp(RegisterRequest registerRequest) {
        AppUser appUser = new AppUser();


        // CHECK IF USER ALREADY EXISTS WITH THAT EMAIL, IF EXISTS, THEN THROW ERROR
        if(userRepository.findAppUserByEmail(registerRequest.getEmail()).isPresent()) {
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

        // ELSE IF THE EXPIRATION DATE IS PASSED
        if(verificationToken.getExpirationDate() != null && verificationToken.getExpirationDate().isBefore( Instant.now())) {
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
}
