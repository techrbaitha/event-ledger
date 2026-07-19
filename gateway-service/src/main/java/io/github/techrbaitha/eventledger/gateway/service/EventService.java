package io.github.techrbaitha.eventledger.gateway.service;

import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.EventResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    public EventResponse processEvent(EventRequest request) {

        log.info("Received eventId={}, accountId={}, type={}, amount={}",
                request.eventId(),
                request.accountId(),
                request.type(),
                request.amount());

        return new EventResponse(
                request.eventId(),
                "ACCEPTED",
                "Event accepted successfully."
        );
    }
}