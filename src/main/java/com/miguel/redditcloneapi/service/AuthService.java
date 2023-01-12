package com.miguel.redditcloneapi.service;


import com.miguel.redditcloneapi.dto.RegisterRequest;
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


        //TODO CHECK IF USER ALREADY EXISTS WITH THAT EMAIL

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
}
