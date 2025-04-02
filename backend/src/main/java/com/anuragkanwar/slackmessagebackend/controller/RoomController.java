package com.anuragkanwar.slackmessagebackend.controller;

import com.anuragkanwar.slackmessagebackend.model.dto.request.CreateRoomRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.UpdateRoomRequestDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.UpdateUserInRoomDto;
import com.anuragkanwar.slackmessagebackend.model.dto.request.UpdateUsersInRoomRequestDto;
import com.anuragkanwar.slackmessagebackend.service.ChatService;
import com.anuragkanwar.slackmessagebackend.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;
    private final ChatService chatService;

    public RoomController(RoomService roomService, ChatService chatService) {
        this.roomService = roomService;
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<?> createNewRoom(@RequestBody CreateRoomRequestDto requestDto) {
        roomService.save(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok()
                .body(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoomById(@PathVariable Long roomId, @RequestHeader("X-workspace-id") String workspaceId) {
        roomService.deleteRoom(roomId, workspaceId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{roomId}")
    public ResponseEntity<?> updateRoom(
            @RequestBody UpdateRoomRequestDto requestDto,
            @PathVariable Long roomId,
            @RequestHeader("X-workspace-id") String workspaceId
    ) {
        roomService.updateRoom(requestDto, roomId, workspaceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomId}/history")
    public ResponseEntity<?> getRoomWithChatsById(
            @PathVariable Long roomId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok()
                .body(chatService.getRoomWithChatsById(roomId, cursor, size));
    }

    @PatchMapping("/{roomId}/updateUsers")
    public ResponseEntity<?> updateUsersInRoom(@PathVariable Long roomId, @RequestBody UpdateUsersInRoomRequestDto updateUsersRequestDto){
        roomService.updateUsersInRoom(roomId, updateUsersRequestDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{roomId}/user")
    public ResponseEntity<?> addUserToRoom(
            @RequestBody UpdateUserInRoomDto updateUserInRoomDto,
            @PathVariable Long roomId
    ) {
        roomService.addUserToRoom(updateUserInRoomDto.getUserId(), roomId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roomId}/user")
    public ResponseEntity<?> removeUserFromRoom(
            @RequestBody UpdateUserInRoomDto updateUserInRoomDto,
            @PathVariable Long roomId
    ) {
        roomService.removeUserFromRoom(updateUserInRoomDto.getUserId(), roomId);
        return ResponseEntity.ok().build();
    }
}
