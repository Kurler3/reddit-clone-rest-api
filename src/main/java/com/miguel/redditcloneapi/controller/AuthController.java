package com.miguel.redditcloneapi.controller;


import com.miguel.redditcloneapi.dto.RegisterRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {


    // LOGIN

    // SIGN UP
    @PostMapping("/sign-up")
    public void signUp(
            @RequestBody RegisterRequest registerRequest
    ) {

    }

    // LOGOUT
}
