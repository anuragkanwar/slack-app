package com.anuragkanwar.slackmessagebackend.model.dto.response;

import com.anuragkanwar.slackmessagebackend.model.dto.common.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTypingResponseDto {
    private UserDto user;
    private Boolean isTyping;
}
