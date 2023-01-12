package com.miguel.redditcloneapi.controller;


import com.miguel.redditcloneapi.dto.RegisterRequest;
import com.miguel.redditcloneapi.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    // LOGIN

    // SIGN UP
    @PostMapping("/register")
    public ResponseEntity<String> signUp(
            @RequestBody RegisterRequest registerRequest
    ) {
        authService.signUp(registerRequest);

        return new ResponseEntity<String>("User registration successful", HttpStatus.OK);
    }

    // ACCOUNT VERIFICATION
    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> accountVerification(
            @PathVariable String token
    ) {
            authService.verifyAccount(token);

            return new ResponseEntity<String>("Account activated successfully", HttpStatus.OK);
    }
}
