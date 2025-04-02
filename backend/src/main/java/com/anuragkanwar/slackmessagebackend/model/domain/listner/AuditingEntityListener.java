package com.anuragkanwar.slackmessagebackend.model.domain.listner;

import com.anuragkanwar.slackmessagebackend.model.domain.AbstractAuditingEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
public class AuditingEntityListener {
    @PrePersist
    public void preCreate(final AbstractAuditingEntity auditable) {
        final var now = LocalDateTime.now();
        auditable.setCreatedAt(now);
        auditable.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(final AbstractAuditingEntity auditable) {
        final var now = LocalDateTime.now();
        auditable.setUpdatedAt(now);
    }
}
