package com.anuragkanwar.slackmessagebackend.service.impl;

import com.anuragkanwar.slackmessagebackend.exception.general.ResourceNotFoundException;
import com.anuragkanwar.slackmessagebackend.model.domain.Room;
import com.anuragkanwar.slackmessagebackend.model.domain.User;
import com.anuragkanwar.slackmessagebackend.repository.UserRepository;
import com.anuragkanwar.slackmessagebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User insertNewUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<Room> findAllRoomsByUserId(Long userId) {
        return userRepository.findAllRoomsByUserId(userId);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User does not exists with username: " + username)
        );
    }


    @Override
    public List<User> findByWorkspaces_Id(Long workspacesId) {
        return userRepository.findByWorkspaces_Id(workspacesId);
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.getUserById(id).orElseThrow(() ->
                new ResourceNotFoundException("User does not exists with id: " + id)
        );
    }
}
