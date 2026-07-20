package io.github.techrbaitha.eventledger.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<String> handleDuplicateTransactionException(
            DuplicateTransactionException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}