package com.anuragkanwar.slackmessagebackend.configuration;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServerCommandLineRunner implements CommandLineRunner {

    private final SocketIOServer server;

    public ServerCommandLineRunner(SocketIOServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {
        if(log.isDebugEnabled()){
            log.debug("Inside SeverCommandLineRunner");
        }
        server.start();
    }
}