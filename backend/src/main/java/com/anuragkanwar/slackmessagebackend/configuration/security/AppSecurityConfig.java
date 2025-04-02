package com.anuragkanwar.slackmessagebackend.configuration.security;

import com.anuragkanwar.slackmessagebackend.configuration.security.jwt.AuthEntryPointJwt;
import com.anuragkanwar.slackmessagebackend.configuration.security.jwt.AuthTokenFilter;
import com.anuragkanwar.slackmessagebackend.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                   OrderedFormContentFilter formContentFilter) throws Exception {
        return http.csrf(CsrfConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(httpSecurityCorsConfigurer ->
                        httpSecurityCorsConfigurer.configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowCredentials(true);
                            config.setAllowedOrigins(List.of("http://localhost:5173"));
                            config.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin",
                                    "Content-Type", "Accept", "Authorization",
                                    Constants.USERNAME_AUTH_HEADER,
                                    "Origin, Accept", "X-Requested-With", "Access-Control-Request-Method",
                                    "Access-Control-Request-Headers", "X-workspace-id"));
                            config.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept",
                                    "Authorization", Constants.USERNAME_AUTH_HEADER, "X-workspace-id",
                                    "Access-Control" +
                                            "-Allow" +
                                            "-Origin", "Access-Control-Allow-Origin", "Access" +
                                            "-Control-Allow" +
                                            "-Credentials"));
                            config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE",
                                    "OPTIONS", "HEAD"));
                            config.setMaxAge(3600 * 7L);

                            return config;
                        }))
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/socket.io/**").permitAll()
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated())
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(unauthorizedHandler))
                .addFilterBefore(authenticationJwtTokenFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
