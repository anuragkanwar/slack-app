package com.anuragkanwar.slackmessagebackend.model.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Base64;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCursor {
    private LocalDateTime createdAt;
    private Long id;

    // Decodes a Base64 string to a cursor
    public static ChatCursor decode(String encodedCursor) {
        String decoded = new String(Base64.getDecoder().decode(encodedCursor));
        String[] parts = decoded.split(":");
        return new ChatCursor(
                LocalDateTime.parse(parts[0]),
                Long.parseLong(parts[1])
        );
    }

    // Encodes cursor to a Base64 string
    public String encode() {
        String cursorString = createdAt.toString() + ":" + id;
        return Base64.getEncoder().encodeToString(cursorString.getBytes());
    }


}
