package com.anuragkanwar.slackmessagebackend.utils;

import com.anuragkanwar.slackmessagebackend.configuration.security.service.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Utils {
    public static String getCurrentUsernameFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return principal.toString();
            }
        }
        return null;
    }


    public static Long getCurrentUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                return ((UserDetailsImpl) principal).getId();
            } else {
                return null;
            }
        }
        return null;
    }

    public static String getUserRoom(String userId) {
        return "user_" + userId;
    }

    public static String getChannelRoom(String channelId) {
        return "channel_" + channelId;
    }

    public static String getWorkspaceRoom(String workspaceId) {
        return "workspace_" + workspaceId;
    }


    public static String encodeToBase64(String str) {
        if (str == null)
            return null;
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String decodeFromBase64(String str) {
        if (str == null)
            return null;
        return new String(Base64.getDecoder().decode(str), StandardCharsets.UTF_8);
    }

    public Long getCurrentWorkspaceIdFromCookie() {
        return null;
    }
}
