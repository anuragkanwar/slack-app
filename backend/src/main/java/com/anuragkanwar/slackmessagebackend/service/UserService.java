package com.anuragkanwar.slackmessagebackend.service;

import com.anuragkanwar.slackmessagebackend.model.domain.Room;
import com.anuragkanwar.slackmessagebackend.model.domain.User;

import java.util.List;

public interface UserService {
    User insertNewUser(User user);

    List<Room> findAllRoomsByUserId(Long userId);

    User save(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User getUserByUsername(String username);

    User getUserById(Long id);

    List<User> findByWorkspaces_Id(Long workspacesId);
}
