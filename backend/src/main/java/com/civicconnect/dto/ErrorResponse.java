package com.civicconnect.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error body returned by the API.
 *
 * @param timestamp   when the error occurred (UTC)
 * @param status      HTTP status code
 * @param error       HTTP status reason phrase
 * @param message     human-readable explanation
 * @param path        request path that caused the error
 * @param fieldErrors validation errors keyed by field name (empty when not applicable)
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {
}
