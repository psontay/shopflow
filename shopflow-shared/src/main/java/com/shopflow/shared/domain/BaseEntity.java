package com.shopflow.shared.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BaseEntity {

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private transient final List<DomainEvent> domainEvents = new ArrayList<>();
    
    protected BaseEntity(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Entity Id cannot be null");
        }
        this.id = id;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    protected BaseEntity(UUID id, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected void registerEvent(DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
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

    protected void maskAsUpdated() {
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseEntity that = (BaseEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
