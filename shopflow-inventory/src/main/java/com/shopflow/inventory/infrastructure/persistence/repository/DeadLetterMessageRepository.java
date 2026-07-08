package com.shopflow.inventory.infrastructure.persistence.repository;

import com.shopflow.inventory.infrastructure.persistence.entity.DeadLetterMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeadLetterMessageRepository extends JpaRepository<DeadLetterMessage, UUID> {
}
