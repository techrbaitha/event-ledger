package io.github.techrbaitha.eventledger.gateway.repository;

import io.github.techrbaitha.eventledger.gateway.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    boolean existsByEventId(String eventId);

    Optional<EventEntity> findByEventId(String eventId);
}