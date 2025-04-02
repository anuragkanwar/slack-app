package com.anuragkanwar.slackmessagebackend.socket.clientHandlers;

import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;
import com.anuragkanwar.slackmessagebackend.socket.model.BaseEvent;
import com.corundumstudio.socketio.listener.DataListener;

public interface ClientEventHandler<T> {
    DataListener<T> handleEvent();
    SocketEvent getIngressEventType();
    SocketEvent getEgressEventType();
    Class<T> getIngresDataType();
}
