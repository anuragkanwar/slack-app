package com.anuragkanwar.slackmessagebackend.repository;

import com.anuragkanwar.slackmessagebackend.model.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByRoom_Id(Long room_id);

    Optional<Chat> getChatById(Long id);

    @Query(value = """
                 WITH user_chats AS (
                     SELECT id, created_at
                     FROM chat
                     WHERE
                         room_id = :roomId
                         AND chat_type = 'USER'
                         AND (created_at < :cursorCreatedAt OR
                             (created_at = :cursorCreatedAt AND id < :cursorId))
                     ORDER BY created_at DESC, id DESC
                     LIMIT :pageSize
                 )
                 SELECT * FROM chat
                 WHERE
                     room_id = :roomId
                     AND (
                         (chat_type = 'USER' AND id IN (SELECT id FROM user_chats))
                         OR
                         (chat_type = 'SERVER' AND created_at >= (
                             SELECT MIN(created_at) FROM user_chats
                         ) AND created_at < :cursorCreatedAt
                     )
                 )
                 ORDER BY created_at DESC, id DESC
            """, nativeQuery = true)
    List<Chat> findNextPage(
            @Param("roomId") Long roomId,
            @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
            @Param("cursorId") Long cursorId,
            @Param("pageSize") int pageSize
    );


    @Query(value = """
                WITH user_chats AS (
                    SELECT id, created_at
                    FROM chat
                    WHERE room_id = :roomId AND chat_type = 'USER'
                    ORDER BY created_at DESC, id DESC
                    LIMIT :pageSize
                )
                SELECT * FROM chat
                WHERE
                    room_id = :roomId
                    AND (
                        (chat_type = 'USER' AND id IN (SELECT id FROM user_chats))
                        OR
                        (chat_type = 'SERVER' AND created_at >= (
                            SELECT MIN(created_at) FROM user_chats
                        )
                    )
                )
                ORDER BY created_at DESC, id DESC
            """, nativeQuery = true)
    List<Chat> findFirstPage(
            @Param("roomId") Long roomId,
            @Param("pageSize") int pageSize
    );
}

