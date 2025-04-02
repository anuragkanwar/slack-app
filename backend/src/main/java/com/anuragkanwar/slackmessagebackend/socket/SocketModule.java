package com.anuragkanwar.slackmessagebackend.socket;

import com.anuragkanwar.slackmessagebackend.configuration.security.service.UserDetailsImpl;
import com.anuragkanwar.slackmessagebackend.constants.Constants;
import com.anuragkanwar.slackmessagebackend.exception.general.BadRequestException;
import com.anuragkanwar.slackmessagebackend.model.domain.Room;
import com.anuragkanwar.slackmessagebackend.model.dto.common.RoomDto;
import com.anuragkanwar.slackmessagebackend.model.dto.common.UserDto;
import com.anuragkanwar.slackmessagebackend.model.enums.EventType;
import com.anuragkanwar.slackmessagebackend.service.UserService;
import com.anuragkanwar.slackmessagebackend.service.WorkspaceService;
import com.anuragkanwar.slackmessagebackend.socket.model.BootRoomListEvent;
import com.anuragkanwar.slackmessagebackend.socket.model.BootUserListEvent;
import com.anuragkanwar.slackmessagebackend.socket.model.HelloEvent;
import com.anuragkanwar.slackmessagebackend.utils.Utils;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SocketModule {

    public final SocketIOServer server;
    public final SocketService socketService;
    private final UserService userService;
    private final SocketRoomManager socketRoomManager;
    private final SocketSessionManager socketSessionManager;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final WorkspaceService workspaceService;

    public SocketModule(
            UserService userService,
            SocketIOServer server,
            SocketService socketService,
            SocketRoomManager socketRoomManager,
            SocketSessionManager socketSessionManager,
            ApplicationEventPublisher applicationEventPublisher,
            WorkspaceService workspaceService
    ) {
        log.info("Inside SocketModule Constructor");
        this.userService = userService;
        this.server = server;
        this.socketService = socketService;
        this.socketRoomManager = socketRoomManager;
        this.applicationEventPublisher = applicationEventPublisher;
        this.socketSessionManager = socketSessionManager;
        this.workspaceService = workspaceService;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
    }


    private ConnectListener onConnected() {
        log.info("Inside OnConnected");
        return (client) -> {

            var params = client.getHandshakeData().getUrlParams();

            UserDetailsImpl userDetails = client.get("user");

            String username = userDetails.getUsername();

            String workspaceId = Utils.decodeFromBase64(client.getHandshakeData().getHttpHeaders().get("X-workspace-id"));
            if (log.isDebugEnabled()) {
                log.debug("userDetails: {}, workspaceId: {}", userDetails, workspaceId);
                log.debug("Workspace Id: {}", Utils.getWorkspaceRoom(workspaceId));
                log.debug("User Id: {}", Utils.getUserRoom(userDetails.getId().toString()));
            }

            if (workspaceId == null) {
                throw new BadRequestException("Workspace Header is missing");
            }

            socketSessionManager.addSession(userDetails.getId(), client.getSessionId());


            socketRoomManager.joinRoom(client.getSessionId(),
                    Utils.getUserRoom(userDetails.getId().toString()));

            socketRoomManager.joinRoom(client.getSessionId(),
                    Utils.getWorkspaceRoom(workspaceId));

            List<Room> rooms = userService.findAllRoomsByUserId(userDetails.getId());

            for (Room room : rooms) {
                socketRoomManager.joinRoom(client.getSessionId(),
                        Utils.getChannelRoom(room.getId().toString()));

                socketService.saveEventLog(
                        String.format(Constants.WELCOME_MESSAGE, username),
                        room.getId().toString(),
                        EventType.SERVER);
            }

            applicationEventPublisher.publishEvent(new HelloEvent(
                    this,
                    "Hello",
                    Utils.getUserRoom(userDetails.getId().toString())
            ));

            applicationEventPublisher.publishEvent(new BootRoomListEvent(
                    this,
                    userService.findAllRoomsByUserId(userDetails.getId())
                            .stream().map(RoomDto::toDtoSmall).toList(),
                    Utils.getUserRoom(userDetails.getId().toString())
            ));

            applicationEventPublisher.publishEvent(new BootUserListEvent(
                    this,
                    userService.findByWorkspaces_Id(Long.valueOf(workspaceId)).stream().map(UserDto::ToDtoSmall).toList(),
                    Utils.getUserRoom(userDetails.getId().toString())
            ));
        };
    }

    private DisconnectListener onDisconnected() {
        log.info("Inside OnDisconnected");
        return client -> {
            UserDetailsImpl userDetails = client.get(
                    "user");
            String username = userDetails.getUsername();
            String room = "All";
            socketService.saveEventLog(String.format(Constants.WELCOME_MESSAGE, username), room,
                    EventType.SERVER);
            log.info("Socket ID[{}] - room[{}] - username" +
                            " [{}]  disconnected " +
                            "to chat module through",
                    client.getSessionId().toString(),
                    room, username);
        };
    }

}
