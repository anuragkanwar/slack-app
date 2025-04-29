package com.anuragkanwar.slackmessagebackend.service.impl;

import com.anuragkanwar.slackmessagebackend.configuration.security.jwt.JwtUtils;
import com.anuragkanwar.slackmessagebackend.configuration.security.service.UserDetailsImpl;
import com.anuragkanwar.slackmessagebackend.exception.general.BadRequestException;
import com.anuragkanwar.slackmessagebackend.exception.general.ResourceNotFoundException;
import com.anuragkanwar.slackmessagebackend.model.domain.User;
import com.anuragkanwar.slackmessagebackend.model.domain.Workspace;
import com.anuragkanwar.slackmessagebackend.model.dto.common.WorkspaceDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.LoginRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.SignupRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.response.AuthResponseDto;
import com.anuragkanwar.slackmessagebackend.repository.WorkspaceRepository;
import com.anuragkanwar.slackmessagebackend.service.AuthService;
import com.anuragkanwar.slackmessagebackend.service.UserService;
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
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    final AuthenticationManager authenticationManager;
    final UserService userService;
    final PasswordEncoder encoder;
    final JwtUtils jwtUtils;
    final WorkspaceRepository workspaceRepository;

    @Value("${socketio.host}")
    private String socketioHost;
    @Value("${socketio.port}")
    private int socketioPort;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserService userService,
                           PasswordEncoder encoder,
                           JwtUtils jwtUtils, WorkspaceRepository workspaceRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.workspaceRepository = workspaceRepository;
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
        } catch (
                AuthenticationException e) {
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
        Set<Workspace> workspaces = workspaceRepository.findByUsers_Id(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .body(AuthResponseDto.builder()
                        .id(userDetails.getId())
                        .username(userDetails.getUsername())
                        .avatarUrl("https://api.dicebear.com/9.x/pixel-art-neutral/svg?seed=" + userDetails.getUsername())
                        .email(userDetails.getEmail())
                        .wsUrl(websocketUrl)
                        .workspaces(WorkspaceDto.workspaceSetToWorkspaceDtoSet(workspaces))
                        .token(jwtToken)
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
                .avatarUrl("https://api.dicebear.com/9.x/pixel-art-neutral/svg?seed=" + signUpRequestDto.getUsername())
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

        Set<Workspace> workspaces = workspaceRepository.findByUsers_Id(savedUser.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .body(AuthResponseDto.builder()
                        .id(savedUser.getId())
                        .username(savedUser.getUsername())
                        .avatarUrl(savedUser.getAvatarUrl())
                        .email(savedUser.getEmail())
                        .wsUrl(websocketUrl)
                        .token(jwtToken)
                        .workspaces(WorkspaceDto.workspaceSetToWorkspaceDtoSet(workspaces))
                        .build());
    }

    @Override
    public ResponseEntity<?> getMe(String jwtToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String websocketUrl = String.format("ws://%s:%d", socketioHost, socketioPort);
        Set<Workspace> workspaces = workspaceRepository.findByUsers_Id(userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(AuthResponseDto.builder()
                        .id(userDetails.getId())
                        .avatarUrl("https://api.dicebear.com/9.x/pixel-art-neutral/svg?seed=" + userDetails.getUsername())
                        .username(userDetails.getUsername())
                        .email(userDetails.getEmail())
                        .wsUrl(websocketUrl)
                        .token(jwtToken)
                        .workspaces(WorkspaceDto.workspaceSetToWorkspaceDtoSet(workspaces))
                        .build());
    }
}
