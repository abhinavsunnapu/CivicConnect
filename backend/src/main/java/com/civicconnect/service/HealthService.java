package com.civicconnect.service;

import com.civicconnect.config.AppProperties;
import com.civicconnect.dto.HealthResponse;
import org.springframework.stereotype.Service;

/**
 * Builds the application health summary.
 */
@Service
public class HealthService {

    private static final String STATUS_UP = "UP";

    private final AppProperties appProperties;

    public HealthService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public HealthResponse getHealth() {
        return new HealthResponse(STATUS_UP, appProperties.name(), appProperties.version());
    }
}
