package com.anuragkanwar.slackmessagebackend.service;

import com.anuragkanwar.slackmessagebackend.model.domain.Chat;
import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateChatRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.UpdateChatRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.response.ChatHistoryResponseDto;

public interface ChatService {


    void deleteChat(Long chatId);

    Chat saveChat(CreateChatRequestDto chat);

    Chat updateChat(Long chatId, UpdateChatRequestDto chat);

    ChatHistoryResponseDto getRoomWithChatsById(Long roomId, String encodedCursor,
                                                int size);
}
