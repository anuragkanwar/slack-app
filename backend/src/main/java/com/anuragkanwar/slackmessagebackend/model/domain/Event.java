package com.anuragkanwar.slackmessagebackend.model.domain;

import com.anuragkanwar.slackmessagebackend.model.enums.EventType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event extends AbstractAuditingEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String message;

    @OneToOne
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    private EventType eventType;
}
