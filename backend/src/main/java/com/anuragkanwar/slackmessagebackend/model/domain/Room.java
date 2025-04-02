package com.anuragkanwar.slackmessagebackend.model.domain;

import com.anuragkanwar.slackmessagebackend.model.enums.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;


@SuperBuilder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Room extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @OneToMany
    @Builder.Default
    private Set<Chat> chats = new HashSet<>();

    @ManyToOne
    private Workspace workspace;

    @ManyToOne
    private User creator;

    @ManyToMany
    @Builder.Default
    private Set<User> users = new HashSet<>();
}
