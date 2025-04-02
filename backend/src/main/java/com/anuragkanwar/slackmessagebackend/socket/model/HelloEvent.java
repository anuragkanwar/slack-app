package com.anuragkanwar.slackmessagebackend.socket.model;


import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import lombok.Getter;

@Getter
public class HelloEvent extends BaseEvent<String> {
    public HelloEvent(Object source, String data, String socketRoom) {
        super(source, SocketEvent.hello, data, socketRoom);
    }
}
