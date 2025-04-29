package com.anuragkanwar.slackmessagebackend.configuration;

import com.anuragkanwar.slackmessagebackend.configuration.security.jwt.JwtUtils;
import com.anuragkanwar.slackmessagebackend.configuration.security.service.UserDetailServiceImpl;
import com.anuragkanwar.slackmessagebackend.constants.Constants;
import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.SocketIOServer;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class SocketIOConfig {

    private final JwtUtils jwtUtils;
    private final UserDetailServiceImpl userDetailService;
    @Value("${socketio.host}")
    private String socketioHost;
    @Value("${socketio.port}")
    private int socketioPort;

    public SocketIOConfig(JwtUtils jwtUtils, UserDetailServiceImpl userDetailService) {
        this.jwtUtils = jwtUtils;
        this.userDetailService = userDetailService;
    }

    @Bean
    public SocketIOServer socketIOServer() {

        log.info("inside constructor");
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setPort(socketioPort);
        config.setHostname(socketioHost);
        config.setEnableCors(true);
        config.setOrigin("http://localhost:3000");
        config.setAllowHeaders(String.join(",", "Origin", "Access-Control-Allow-Origin",
                "Content-Type", "Accept", "Authorization",
                Constants.USERNAME_AUTH_HEADER,
                "Origin, Accept", "X-Requested-With", "Access-Control-Request-Method",
                "Access-Control-Request-Headers", "X-workspace-id"));

        config.setAuthorizationListener(handshakeData -> {

            if (log.isDebugEnabled()) {
                log.debug("Handshake data : {}", handshakeData.getHttpHeaders().toString());
            }

            HttpHeaders headers = handshakeData.getHttpHeaders();
            String jwt = jwtUtils.getJwtTokenFromAuthString(headers.get("Authorization"));
            if (log.isDebugEnabled()) {
                log.debug("jwt : {}", jwt);
            }
            UserDetails userDetails = null;
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                if (log.isDebugEnabled()) {
                    log.debug("socket validation passed");
                }
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                userDetails = userDetailService.loadUserByUsername(username);
                Map<String, Object> authResult = new HashMap<>();
                authResult.put("user", userDetails);
                return new AuthorizationResult(true, authResult);
            }
            if (log.isDebugEnabled()) {
                log.debug("socket validation failed or jwt is null");
            }
            if (handshakeData.getHttpHeaders().contains("Access-Control-Request-Method")) {
                return new AuthorizationResult(true, null);
            }
            if (log.isDebugEnabled()) {
                log.debug("not a pre flight request");
            }
            return AuthorizationResult.FAILED_AUTHORIZATION;
        });

        return new SocketIOServer(config);
    }

}
