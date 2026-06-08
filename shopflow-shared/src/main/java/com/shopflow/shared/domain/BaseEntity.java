package com.shopflow.shared.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class BaseEntity {

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    protected BaseEntity(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Entity Id cannot be null");
        }
        this.id = id;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
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
