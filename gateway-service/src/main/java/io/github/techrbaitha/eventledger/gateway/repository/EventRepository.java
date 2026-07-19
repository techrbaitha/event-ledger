package io.github.techrbaitha.eventledger.gateway.repository;

import io.github.techrbaitha.eventledger.gateway.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    Optional<EventEntity> findByEventId(String eventId);

    boolean existsByEventId(String eventId);
}