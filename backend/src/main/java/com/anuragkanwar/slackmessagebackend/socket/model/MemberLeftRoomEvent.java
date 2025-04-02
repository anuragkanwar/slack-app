package com.anuragkanwar.slackmessagebackend.socket.model;


import com.anuragkanwar.slackmessagebackend.model.dto.common.RoomDto;
import com.anuragkanwar.slackmessagebackend.model.dto.common.UserDto;
import com.anuragkanwar.slackmessagebackend.model.dto.response.MemberLeaveResponseDto;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import lombok.Getter;

@Getter
public class MemberLeftRoomEvent extends BaseEvent<MemberLeaveResponseDto> {
    public MemberLeftRoomEvent(Object source, MemberLeaveResponseDto data, String socketRoom) {
        super(source, SocketEvent.member_left_room, data, socketRoom);
    }
}
