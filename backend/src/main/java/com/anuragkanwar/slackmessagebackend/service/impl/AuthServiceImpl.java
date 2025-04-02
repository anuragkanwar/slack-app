package com.anuragkanwar.slackmessagebackend.service.impl;

import com.anuragkanwar.slackmessagebackend.configuration.security.jwt.JwtUtils;
import com.anuragkanwar.slackmessagebackend.configuration.security.service.UserDetailsImpl;
import com.anuragkanwar.slackmessagebackend.exception.general.BadRequestException;
import com.anuragkanwar.slackmessagebackend.exception.general.ResourceNotFoundException;
import com.anuragkanwar.slackmessagebackend.model.domain.User;
import com.anuragkanwar.slackmessagebackend.model.dto.request.LoginRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.SignupRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.response.AuthResponseDto;
import com.anuragkanwar.slackmessagebackend.service.AuthService;
import com.anuragkanwar.slackmessagebackend.service.UserService;
import com.anuragkanwar.slackmessagebackend.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class AuthServiceImpl implements AuthService {

    final AuthenticationManager authenticationManager;
    final UserService userService;
    final WorkspaceService workspaceService;
    final PasswordEncoder encoder;
    final JwtUtils jwtUtils;

    @Value("${socketio.host}")
    private String socketioHost;
    @Value("${socketio.port}")
    private int socketioPort;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserService userService,
                           WorkspaceService workspaceService, PasswordEncoder encoder,
                           JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.workspaceService = workspaceService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public ResponseEntity<?> login(LoginRequestDto loginRequestDto) {
        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername()
                                    , loginRequestDto.getPassword()));
        } catch (AuthenticationException e) {
            throw new ResourceNotFoundException("Bad Credentials");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUserDetails(userDetails);

        ResponseCookie cookie = ResponseCookie.from("token", jwtToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        String websocketUrl = String.format("ws://%s:%d", socketioHost, socketioPort);

        return ResponseEntity.status(HttpStatus.OK)
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .body(AuthResponseDto.builder()
                        .id(userDetails.getId())
                        .username(userDetails.getUsername())
                        .email(userDetails.getEmail())
                        .wsUrl(websocketUrl)
                        .build());
    }

    @Override
    @Transactional
    public ResponseEntity<?> signup(SignupRequestDto signUpRequestDto) {
        if (userService.existsByUsername(signUpRequestDto.getUsername())) {
            throw new BadRequestException("Error: Username is already taken!");
        }

        if (userService.existsByEmail(signUpRequestDto.getEmail())) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        User user = User.builder()
                .username(signUpRequestDto.getUsername())
                .email(signUpRequestDto.getEmail())
                .password(encoder.encode(signUpRequestDto.getPassword()))
                .build();

        User savedUser = userService.save(user);

        String jwtToken = jwtUtils.generateTokenFromUserDetails(UserDetailsImpl.builder()
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .id(savedUser.getId())
                .authorities(null)
                .password(savedUser.getPassword())
                .build());

        ResponseCookie cookie = ResponseCookie.from("token", jwtToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        String websocketUrl = String.format("ws://%s:%d", socketioHost, socketioPort);

        return ResponseEntity.status(HttpStatus.OK)
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .body(AuthResponseDto.builder()
                        .id(savedUser.getId())
                        .username(savedUser.getUsername())
                        .email(savedUser.getEmail())
                        .wsUrl(websocketUrl)
                        .build());
    }

    @Override
    public ResponseEntity<?> getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String websocketUrl = String.format("ws://%s:%d", socketioHost, socketioPort);

        return ResponseEntity.status(HttpStatus.OK)
                .body(AuthResponseDto.builder()
                        .id(userDetails.getId())
                        .username(userDetails.getUsername())
                        .email(userDetails.getEmail())
                        .wsUrl(websocketUrl)
                        .build());
    }
}
