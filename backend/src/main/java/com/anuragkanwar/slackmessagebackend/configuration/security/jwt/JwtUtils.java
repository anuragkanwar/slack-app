package com.anuragkanwar.slackmessagebackend.configuration.security.jwt;

import com.anuragkanwar.slackmessagebackend.constants.Constants;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    public String getJWTFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("Auth Header : {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getJwtTokenFromAuthString(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getJWTFromCookies(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(ck -> "token".equals(ck.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public String getWorkspaceIdFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(ck -> "workspace-id".equals(ck.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public String generateTokenFromUserDetails(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + Constants.JWT_TOKEN_EXPIRES_AFTER))
                .signWith(this.key())
                .compact();
    }


    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token).getPayload().getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(Constants.JWT_SECRET_KEY));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            log.info("Validating Token...");
            Jwts.parser().verifyWith((SecretKey) this.key()).build().parseSignedClaims(authToken);
            log.info("Validation Successful...");
            return true;
        } catch (
                MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (
                ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (
                UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (
                IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}