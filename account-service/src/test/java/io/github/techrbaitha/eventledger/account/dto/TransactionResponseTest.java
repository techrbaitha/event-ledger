package io.github.techrbaitha.eventledger.account.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionResponseTest {

    @Test
    void shouldCreateTransactionResponse() {

        TransactionResponse response =
                new TransactionResponse(
                        "SUCCESS",
                        "Transaction applied successfully."
                );

        assertEquals("SUCCESS", response.status());
        assertEquals(
                "Transaction applied successfully.",
                response.message()
        );
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {

        TransactionResponse first =
                new TransactionResponse(
                        "SUCCESS",
                        "Done"
                );

        TransactionResponse second =
                new TransactionResponse(
                        "SUCCESS",
                        "Done"
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.toString().contains("SUCCESS"));
    }
}