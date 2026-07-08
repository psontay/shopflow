package com.shopflow.inventory.infrastructure.persistence.repository;

import com.shopflow.inventory.infrastructure.persistence.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProcessedEventRepository extends JpaRepository<ProcessedEventEntity, String> {

}
