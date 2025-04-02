package com.anuragkanwar.slackmessagebackend.socket.model;


import com.anuragkanwar.slackmessagebackend.model.dto.common.RoomDto;
import com.anuragkanwar.slackmessagebackend.model.dto.response.MemberJoinedResponseDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import lombok.Getter;

@Getter
public class MemberJoinedRoomEvent extends BaseEvent<MemberJoinedResponseDto> {
    public MemberJoinedRoomEvent(Object source, MemberJoinedResponseDto data, String socketRoom) {
        super(source, SocketEvent.member_joined_room, data, socketRoom);
    }
}
