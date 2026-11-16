package com.civicconnect.dto;

/**
 * Response body for {@code GET /api/v1/health}.
 */
public record HealthResponse(String status, String application, String version) {
}
