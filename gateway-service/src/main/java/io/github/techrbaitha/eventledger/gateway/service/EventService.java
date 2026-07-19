package io.github.techrbaitha.eventledger.gateway.service;

import io.github.techrbaitha.eventledger.gateway.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.techrbaitha.eventledger.gateway.client.AccountServiceClient;
import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.EventResponse;
import io.github.techrbaitha.eventledger.gateway.entity.EventEntity;
import io.github.techrbaitha.eventledger.gateway.exception.DuplicateEventException;
import io.github.techrbaitha.eventledger.gateway.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class EventService {

    private final EventRepository repository;
    private final AccountServiceClient accountServiceClient;

    public EventService(
            EventRepository repository,
            AccountServiceClient accountServiceClient) {

        this.repository = repository;
        this.accountServiceClient = accountServiceClient;
    }

    public EventResponse accountServiceFallback(
            EventRequest request,
            Exception ex) {

        log.error(
                "Account Service unavailable. eventId={}, reason={}",
                request.eventId(),
                ex.getMessage()
        );

        throw new ServiceUnavailableException(
                "Account Service is currently unavailable.");
    }

    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "accountServiceFallback"
    )
    public EventResponse processEvent(EventRequest request) {

        log.info(
                "Event persisted successfully. eventId={}",
                request.eventId()
        );

        EventEntity entity = new EventEntity(
                request.eventId(),
                request.accountId(),
                request.type(),
                request.amount(),
                request.currency(),
                request.eventTimestamp()
        );

        try {

            // Persist in Gateway DB
            repository.saveAndFlush(entity);

            // Forward transaction to Account Service
            accountServiceClient.applyTransaction(
                    request.accountId(),
                    request
            );

            log.info("Event persisted and transaction applied. eventId={}",
                    request.eventId());

            return new EventResponse(
                    request.eventId(),
                    "ACCEPTED",
                    "Event accepted successfully."
            );

        } catch (DataIntegrityViolationException ex) {

            log.warn("Duplicate event received. eventId={}", request.eventId());

            throw new DuplicateEventException(request.eventId());
        }
    }
}