package com.anuragkanwar.slackmessagebackend.service.impl;

import com.anuragkanwar.slackmessagebackend.exception.general.ForbiddenException;
import com.anuragkanwar.slackmessagebackend.exception.general.ResourceNotFoundException;
import com.anuragkanwar.slackmessagebackend.model.domain.Chat;
import com.anuragkanwar.slackmessagebackend.model.domain.Room;
import com.anuragkanwar.slackmessagebackend.model.domain.User;
import com.anuragkanwar.slackmessagebackend.model.dto.common.ChatCursor;
import com.anuragkanwar.slackmessagebackend.model.dto.common.ChatDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateChatRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.UpdateChatRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.response.ChatHistoryResponseDto;
import com.anuragkanwar.slackmessagebackend.model.enums.ChatType;
import com.anuragkanwar.slackmessagebackend.repository.ChatRepository;
import com.anuragkanwar.slackmessagebackend.repository.RoomRepository;
import com.anuragkanwar.slackmessagebackend.repository.UserRepository;
import com.anuragkanwar.slackmessagebackend.service.ChatService;
import com.anuragkanwar.slackmessagebackend.socket.model.MessageEvent;
import com.anuragkanwar.slackmessagebackend.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ChatServiceImpl(ChatRepository chatRepository,
                           RoomRepository roomRepository,
                           UserRepository userRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.chatRepository = chatRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @Override
    public ChatHistoryResponseDto getRoomWithChatsById(Long roomId, String encodedCursor,
                                                       int size) {

        size = Math.min(size, 20);
        List<Chat> chats;

        if (encodedCursor == null) {
            chats = chatRepository.findFirstPage(roomId, size);
        } else {
            ChatCursor cursor = ChatCursor.decode(encodedCursor);
            chats = chatRepository.findNextPage(
                    roomId,
                    cursor.getCreatedAt(),
                    cursor.getId(),
                    size
            );
        }

        Optional<Chat> lastUserChat = chats.stream()
                .filter(c -> c.getChatType() == ChatType.USER)
                .reduce((first, second) -> second); // Get last user chat

        String nextCursor = lastUserChat
                .map(c -> new ChatCursor(c.getCreatedAt(), c.getId()).encode())
                .orElse(null);

        return ChatHistoryResponseDto.builder()
                .nextCursor(nextCursor)
                .chats(chats.stream().map(ChatDto::toDto).toList())
                .build();
    }

    @Override
    public void deleteChat(Long chatId) {
        Long userId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
        chatRepository.deleteById(chatId);
    }

    @Transactional
    @Override
    public Chat saveChat(CreateChatRequestDto chatRequestDto) {
        log.info("inside saveMessage");
        Long userId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
        User user = userRepository.getReferenceById(userId);
        Room room = roomRepository.getReferenceById(chatRequestDto.getRoomId());
        Chat toBeSaved = Chat.builder()
                .message(chatRequestDto.getMessage())
                .user(user)
                .room(room)
                .build();

        if (chatRequestDto.getParentId() != null) {
            Chat parent = chatRepository.getReferenceById(chatRequestDto.getParentId());
            toBeSaved.setParent(parent);
        }


        Chat savedChat = chatRepository.save(toBeSaved);

        applicationEventPublisher.publishEvent(new MessageEvent(
                this,
                ChatDto.toDtoSmall(savedChat),
                Utils.getChannelRoom(chatRequestDto.getRoomId().toString())
        ));

        return savedChat;
    }


    @Transactional

    @Override
    public Chat updateChat(Long chatId, UpdateChatRequestDto requestDto) {
        Long userId =
                Utils.getCurrentUserIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());

        Chat chat = chatRepository.getChatById(chatId).orElseThrow(() ->
                new ResourceNotFoundException("Chat does not exists with id: " + chatId));

        if (!userId.equals(chat.getUser().getId())) {
            throw new ForbiddenException("Your Id is not equals to Creator Id");
        }

        if (requestDto.getMessage() != null) {
            chat.setMessage(requestDto.getMessage());
        }

        return chatRepository.save(chat);
    }
}