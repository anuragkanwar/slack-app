package com.anuragkanwar.slackmessagebackend.service;

import com.anuragkanwar.slackmessagebackend.model.dto.request.LoginRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.SignupRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequestDto loginRequestDto);

    ResponseEntity<?> signup(SignupRequestDto signupRequestDto);

    ResponseEntity<?> getMe(String jwtToken);
}
