package com.anuragkanwar.slackmessagebackend.model.domain;

import com.anuragkanwar.slackmessagebackend.model.enums.ChatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(indexes = @Index(
        name = "idx_chat_room_created_at_id",
        columnList = "room_id, chat_type, created_at DESC, id DESC"
))
public class Chat extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    @OneToOne(cascade = CascadeType.ALL)
    private Chat parent;

    @ManyToOne
    private User user;

    @ManyToOne
    private Room room;

}

