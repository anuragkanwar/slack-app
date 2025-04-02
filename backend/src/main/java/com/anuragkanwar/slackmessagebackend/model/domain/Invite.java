package com.anuragkanwar.slackmessagebackend.model.domain;

import com.anuragkanwar.slackmessagebackend.model.enums.InviteStatus;
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
public class Invite extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User inviter;

    @ManyToOne
    private User invitee;

    @ManyToOne
    private Workspace workspace;

    @Enumerated(EnumType.STRING)
    private InviteStatus status;

}
