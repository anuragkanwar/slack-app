package com.anuragkanwar.slackmessagebackend.model.domain;


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
public class Workspace extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @OneToOne
    private User creator;

    @OneToMany
    @Builder.Default
    private Set<Room> rooms = new HashSet<>();
}
