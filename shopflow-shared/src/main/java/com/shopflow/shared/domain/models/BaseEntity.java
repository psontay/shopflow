package com.shopflow.shared.domain.models;

import com.shopflow.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class BaseEntity {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private List<DomainEvent> domainEvents;

    protected BaseEntity(UUID id) {
        this.id = id;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.domainEvents = new ArrayList<>();
    }

    protected BaseEntity(UUID id, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.domainEvents = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<DomainEvent> getDomainEvents() {
        if (domainEvents == null) {
            domainEvents = new ArrayList<>();
        }
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        if (this.domainEvents != null) {
            this.domainEvents.clear();
        }
    }

    protected void markAsUpdated() {
        this.updatedAt = Instant.now();
    }

    protected void addDomainEvent(DomainEvent event) {
        if (this.domainEvents == null) {
            this.domainEvents = new ArrayList<>();
        }
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

}
