package com.anuragkanwar.slackmessagebackend.repository;

import com.anuragkanwar.slackmessagebackend.model.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsRoomByName(String name);

    Optional<Room> getRoomById(Long id);

    Room deleteRoomById(Long id);

    Set<Room> findByWorkspace_Id(Long workspaceId);
}
