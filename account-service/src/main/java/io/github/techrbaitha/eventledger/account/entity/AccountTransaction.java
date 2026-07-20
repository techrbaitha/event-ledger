package io.github.techrbaitha.eventledger.account.entity;

import io.github.techrbaitha.eventledger.account.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "account_transactions",
        uniqueConstraints = @UniqueConstraint(columnNames = "event_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Instant eventTimestamp;

    public AccountTransaction(
            String eventId,
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

}