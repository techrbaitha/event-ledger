package io.github.techrbaitha.eventledger.gateway.service;

import io.github.techrbaitha.eventledger.gateway.client.AccountServiceClient;
import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.EventResponse;
import io.github.techrbaitha.eventledger.gateway.entity.EventEntity;
import io.github.techrbaitha.eventledger.gateway.enums.TransactionType;
import io.github.techrbaitha.eventledger.gateway.exception.ServiceUnavailableException;
import io.github.techrbaitha.eventledger.gateway.repository.EventRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository repository;

    @Mock
    private AccountServiceClient accountServiceClient;

    private EventService eventService;

    private EventRequest request;

    @BeforeEach
    void setup() {

        eventService = new EventService(
                repository,
                accountServiceClient,
                new SimpleMeterRegistry()
        );

        request = new EventRequest(
                "evt-001",
                "acct-001",
                TransactionType.CREDIT,
                new BigDecimal("100.00"),
                "USD",
                Instant.parse("2026-05-15T14:02:11Z")
        );
    }

    @Test
    @DisplayName("Should process event successfully")
    void shouldProcessEventSuccessfully() {

        when(repository.saveAndFlush(any(EventEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EventResponse response = eventService.processEvent(request);

        assertNotNull(response);
        assertEquals("evt-001", response.eventId());
        assertEquals("ACCEPTED", response.status());
        assertEquals("Event accepted successfully.", response.message());

        verify(repository).saveAndFlush(any(EventEntity.class));

        verify(accountServiceClient)
                .applyTransaction(
                        eq("acct-001"),
                        eq(request)
                );
    }

    @Test
    @DisplayName("Should persist correct event details")
    void shouldPersistCorrectEvent() {

        when(repository.saveAndFlush(any(EventEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        eventService.processEvent(request);

        ArgumentCaptor<EventEntity> captor =
                ArgumentCaptor.forClass(EventEntity.class);

        verify(repository).saveAndFlush(captor.capture());

        EventEntity entity = captor.getValue();

        assertEquals("evt-001", entity.getEventId());
        assertEquals("acct-001", entity.getAccountId());
        assertEquals(TransactionType.CREDIT, entity.getType());
        assertEquals(new BigDecimal("100.00"), entity.getAmount());
        assertEquals("USD", entity.getCurrency());
    }

    @Test
    @DisplayName("Should ignore duplicate event")
    void shouldIgnoreDuplicateEvent() {

        when(repository.saveAndFlush(any(EventEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        when(repository.findByEventId("evt-001"))
                .thenReturn(Optional.of(
                        new EventEntity(
                                "evt-001",
                                "acct-001",
                                TransactionType.CREDIT,
                                new BigDecimal("100.00"),
                                "USD",
                                Instant.now()
                        )
                ));

        EventResponse response = eventService.processEvent(request);

        assertEquals("evt-001", response.eventId());
        assertEquals("ACCEPTED", response.status());
        assertEquals("Duplicate event ignored.", response.message());

        verify(repository).findByEventId("evt-001");
        verify(accountServiceClient, never())
                .applyTransaction(anyString(), any());
    }

    @Test
    @DisplayName("Should invoke Account Service exactly once")
    void shouldInvokeAccountServiceOnce() {

        when(repository.saveAndFlush(any(EventEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        eventService.processEvent(request);

        verify(accountServiceClient, times(1))
                .applyTransaction(
                        eq("acct-001"),
                        eq(request)
                );
    }

    @Test
    @DisplayName("Should throw ServiceUnavailableException from fallback")
    void shouldThrowServiceUnavailableException() {

        RuntimeException exception =
                new RuntimeException("Account Service Down");

        ServiceUnavailableException ex =
                assertThrows(
                        ServiceUnavailableException.class,
                        () -> eventService.accountServiceFallback(
                                request,
                                exception
                        )
                );

        assertEquals(
                "Account Service is currently unavailable.",
                ex.getMessage()
        );
    }
}