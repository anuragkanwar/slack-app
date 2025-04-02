package com.anuragkanwar.slackmessagebackend.model.dto.response;

import com.anuragkanwar.slackmessagebackend.model.dto.common.ChatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistoryResponseDto {
    private List<ChatDto> chats;
    private String nextCursor;
}
