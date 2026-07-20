package io.github.techrbaitha.eventledger.gateway.service;

import io.github.techrbaitha.eventledger.gateway.client.AccountServiceClient;
import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.EventResponse;
import io.github.techrbaitha.eventledger.gateway.entity.EventEntity;
import io.github.techrbaitha.eventledger.gateway.enums.TransactionType;
import io.github.techrbaitha.eventledger.gateway.exception.ServiceUnavailableException;
import io.github.techrbaitha.eventledger.gateway.repository.EventRepository;
import io.github.techrbaitha.eventledger.gateway.util.AppConstants;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository repository;

    @Mock
    private AccountServiceClient accountServiceClient;

    private EventService eventService;

    @BeforeEach
    void setUp() {

        eventService = new EventService(
                repository,
                accountServiceClient,
                new SimpleMeterRegistry()
        );
    }

    @Test
    @DisplayName("Should process event successfully")
    void shouldProcessEventSuccessfully() {

        EventRequest request = new EventRequest(
                "EVT-1001",
                "ACC-1001",
                TransactionType.CREDIT,
                BigDecimal.valueOf(1000),
                "INR",
                Instant.parse("2026-07-20T10:15:30Z")
        );

        when(repository.saveAndFlush(any(EventEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EventResponse response = eventService.processEvent(request);

        assertNotNull(response);
        assertEquals("EVT-1001", response.eventId());
        assertEquals("ACCEPTED", response.status());

        verify(repository).saveAndFlush(any(EventEntity.class));

        verify(accountServiceClient)
                .processTransaction(
                        eq("ACC-1001"),
                        eq(request)
                );
    }

    @Test
    @DisplayName("Should return accepted response when duplicate event is received")
    void shouldReturnAcceptedForDuplicateEvent() {

        EventRequest request = new EventRequest(
                "EVT-1001",
                "ACC-1001",
                TransactionType.CREDIT,
                BigDecimal.valueOf(1000),
                "INR",
                Instant.parse("2026-07-20T10:15:30Z")
        );

        EventEntity existingEvent = new EventEntity(
                request.eventId(),
                request.accountId(),
                request.type(),
                request.amount(),
                request.currency(),
                request.eventTimestamp()
        );

        when(repository.saveAndFlush(any(EventEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        when(repository.findByEventId("EVT-1001"))
                .thenReturn(Optional.of(existingEvent));

        EventResponse response = eventService.processEvent(request);

        assertNotNull(response);
        assertEquals("EVT-1001", response.eventId());
        assertEquals("ACCEPTED", response.status());
        assertEquals("Duplicate event ignored.", response.message());

        verify(repository).saveAndFlush(any(EventEntity.class));
        verify(repository).findByEventId("EVT-1001");

        verify(accountServiceClient, never())
                .processTransaction(anyString(), any(EventRequest.class));
    }

    @Test
    @DisplayName("Should throw ServiceUnavailableException from fallback")
    void shouldThrowServiceUnavailableExceptionFromFallback() {

        EventRequest request = new EventRequest(
                "EVT-1001",
                "ACC-1001",
                TransactionType.CREDIT,
                BigDecimal.valueOf(1000),
                "INR",
                Instant.parse("2026-07-20T10:15:30Z")
        );

        RuntimeException cause = new RuntimeException("Account Service Down");

        ServiceUnavailableException exception =
                assertThrows(
                        ServiceUnavailableException.class,
                        () -> eventService.accountServiceFallback(request, cause)
                );

        assertEquals(
                "Account Service is currently unavailable.",
                exception.getMessage()
        );

        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Should increment processed events counter")
    void shouldIncrementProcessedEventsCounter() {

        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

        EventService service = new EventService(
                repository,
                accountServiceClient,
                meterRegistry
        );

        EventRequest request = new EventRequest(
                "EVT-2001",
                "ACC-2001",
                TransactionType.CREDIT,
                BigDecimal.valueOf(500),
                "INR",
                Instant.now()
        );

        when(repository.saveAndFlush(any(EventEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.processEvent(request);

        Counter counter = meterRegistry.find(AppConstants.EVENTS_PROCESSED_METRIC)
                .counter();

        assertNotNull(counter);
        assertEquals(1.0, counter.count());

        verify(repository).saveAndFlush(any(EventEntity.class));
        verify(accountServiceClient)
                .processTransaction(eq("ACC-2001"), eq(request));
    }
}