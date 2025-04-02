package com.anuragkanwar.slackmessagebackend.repository;

import com.anuragkanwar.slackmessagebackend.model.domain.Room;
import com.anuragkanwar.slackmessagebackend.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT r FROM Room r JOIN r.users u where u.id = :userId")
    List<Room> findAllRoomsByUserId(@Param("userId") Long userId);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsername(String username);

    List<User> findByWorkspaces_Id(Long workspacesId);


}
