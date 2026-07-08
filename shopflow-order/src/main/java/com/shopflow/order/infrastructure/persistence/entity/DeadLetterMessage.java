package com.shopflow.order.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dead_letter_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeadLetterMessage {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String topic;

    private String payload;

    private String reason;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private boolean processed = false;
}
