package io.github.techrbaitha.eventledger.gateway.service;

import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.EventResponse;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    public EventResponse processEvent(EventRequest request) {

        return new EventResponse(
                request.getEventId(),
                "ACCEPTED",
                "Event accepted successfully."
        );
    }
}