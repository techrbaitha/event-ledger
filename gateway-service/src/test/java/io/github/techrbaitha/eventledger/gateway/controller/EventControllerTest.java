package io.github.techrbaitha.eventledger.gateway.controller;

import io.github.techrbaitha.eventledger.gateway.dto.EventRequest;
import io.github.techrbaitha.eventledger.gateway.dto.EventResponse;
import io.github.techrbaitha.eventledger.gateway.service.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    @DisplayName("Should accept valid event request")
    void shouldAcceptValidEvent() throws Exception {

        EventResponse response = new EventResponse(
                "EVT-1001",
                "ACCEPTED",
                "Event accepted successfully.",
                Instant.parse("2026-07-20T10:15:30Z")
        );

        when(eventService.processEvent(any(EventRequest.class)))
                .thenReturn(response);

        String request = """
                {
                  "eventId":"EVT-1001",
                  "accountId":"ACC-1001",
                  "type":"CREDIT",
                  "amount":1000,
                  "currency":"INR",
                  "eventTimestamp":"2026-07-20T10:15:30Z"
                }
                """;

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.eventId").value("EVT-1001"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.message")
                        .value("Event accepted successfully."));

        verify(eventService, times(1))
                .processEvent(any(EventRequest.class));
    }

    @Test
    @DisplayName("Should return Bad Request when request is invalid")
    void shouldReturnBadRequestForInvalidRequest() throws Exception {

        String invalidRequest = """
                {
                  "eventId":"",
                  "accountId":"",
                  "type":null,
                  "amount":0,
                  "currency":"",
                  "eventTimestamp":null
                }
                """;

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        verify(eventService, Mockito.never())
                .processEvent(any());
    }
}