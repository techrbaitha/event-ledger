package io.github.techrbaitha.eventledger.gateway.entity;

import io.github.techrbaitha.eventledger.gateway.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "events",
        uniqueConstraints = @UniqueConstraint(columnNames = "event_id"),
        indexes = {
                @Index(name = "idx_event_id", columnList = "event_id"),
                @Index(name = "idx_account_id", columnList = "accountId")
        }
)
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private Instant eventTimestamp;

    protected EventEntity() {
        // Required by JPA
    }

    public EventEntity(String eventId,
                       String accountId,
                       TransactionType type,
                       BigDecimal amount,
                       String currency,
                       Instant eventTimestamp) {
        this.eventId = eventId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.eventTimestamp = eventTimestamp;
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getAccountId() {
        return accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }
}