package com.anuragkanwar.slackmessagebackend.controller;

import com.anuragkanwar.slackmessagebackend.model.dto.request.LoginRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.SignupRequestDto;
import com.anuragkanwar.slackmessagebackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequestDto signUpRequestDto) {
        return authService.signup(signUpRequestDto);
    }

    @GetMapping("/getMe")
    public ResponseEntity<?> getAlreadyLoggedInUser() {
        return authService.getMe();
    }

}
