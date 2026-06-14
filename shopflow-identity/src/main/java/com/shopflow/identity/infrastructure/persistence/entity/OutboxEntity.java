package com.shopflow.identity.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEntity {

    @Id
    private UUID id;
    @Column(nullable = false)
    private String aggregateType;
    @Column(nullable = false)
    private String aggregateId;
    @Column(nullable = false)
    private String eventType;
    @Column(nullable = false,
            columnDefinition = "TEXT")
    private String payload;
    @Column(nullable = false)
    private Instant createdAt;

}
