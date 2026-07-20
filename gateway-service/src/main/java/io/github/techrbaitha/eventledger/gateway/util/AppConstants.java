package io.github.techrbaitha.eventledger.gateway.util;

public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    public static final String EVENTS_PROCESSED_METRIC = "gateway.events.processed";

    public static final String ACCOUNT_SERVICE = "account-service";

    public static final String EVENT_ALREADY_EXISTS = "Duplicate event";

    public static final String INTERNAL_ERROR = "Internal Server Error";

    public static final String TRACE_ID = "traceId";
}