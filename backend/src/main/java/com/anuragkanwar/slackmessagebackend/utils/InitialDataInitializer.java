package com.anuragkanwar.slackmessagebackend.utils;

import com.anuragkanwar.slackmessagebackend.repository.WorkspaceRepository;
import com.anuragkanwar.slackmessagebackend.service.RoomService;
import com.anuragkanwar.slackmessagebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialDataInitializer implements ApplicationRunner {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoomService roomService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /*for (RoleType roleType : RoleType.values()) {
            if (!roleService.existsRoleByRole(roleType)) {
                roleService.save(Role.builder().role(roleType).build());
            }
        }

        System.out.println(workspaceRepository.existsWorkspaceByName("mainWorkspace"));
        if (!workspaceRepository.existsWorkspaceByName("mainWorkspace")) {
            workspaceRepository.save(Workspace.builder().name("mainWorkSpace").build());
        }

        User user1 = User.builder()
                .email("test1gmail.com")
                .password(passwordEncoder.encode("test1"))
                .username("test1")
                .build();

        User user2 = User.builder()
                .email("test2gmail.com")
                .password(passwordEncoder.encode("test2"))
                .username("test2")
                .build();


        if (!userService.existsByEmail("test1@gmail.com")) {
            user1 = userService.save(user1);
        }

        if (!userService.existsByEmail("test2@gmail.com")) {
            user2 = userService.save(user2);
        }

        if (!roomService.existsRoomByName("room1")) {
            roomService.save(Room.builder()
                    .name("room1")
                    .users(new HashSet<>(List.of(user1, user2)))
                    .description("demo room")
                    .is_private(false)
                    .build()
            );
        } */
    }

}
