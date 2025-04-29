package com.anuragkanwar.slackmessagebackend.controller;


import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateChatRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.UpdateChatRequestDto;
import com.anuragkanwar.slackmessagebackend.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<?> postChat(@RequestBody CreateChatRequestDto chat) {
        chatService.saveChat(chat);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{chatId}")
    public ResponseEntity<?> updateChat(@PathVariable Long chatId,
                                        @RequestBody UpdateChatRequestDto chat) {
        chatService.updateChat(chatId, chat);
        return ResponseEntity.ok().build();
    }
}