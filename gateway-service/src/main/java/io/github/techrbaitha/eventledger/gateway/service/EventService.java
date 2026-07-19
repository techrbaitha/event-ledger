package io.github.techrbaitha.eventledger.gateway.service;

import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.EventResponse;
import io.github.techrbaitha.eventledger.gateway.entity.EventEntity;
import io.github.techrbaitha.eventledger.gateway.exception.DuplicateEventException;
import io.github.techrbaitha.eventledger.gateway.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public EventResponse processEvent(EventRequest request) {

        log.info("Received eventId={}", request.eventId());

        EventEntity entity = new EventEntity(
                request.eventId(),
                request.accountId(),
                request.type(),
                request.amount(),
                request.currency(),
                request.eventTimestamp()
        );

        try {

            repository.saveAndFlush(entity);

            log.info("Event persisted successfully eventId={}", request.eventId());

            return new EventResponse(
                    request.eventId(),
                    "ACCEPTED",
                    "Event accepted successfully."
            );

        } catch (DataIntegrityViolationException ex) {

            log.warn("Duplicate event received eventId={}", request.eventId());

            throw new DuplicateEventException(request.eventId());

        }
    }
}