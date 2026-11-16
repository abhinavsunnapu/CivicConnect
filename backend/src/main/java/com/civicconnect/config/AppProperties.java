package com.civicconnect.config;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application settings bound from the {@code civicconnect.*} keys in application.yml.
 *
 * @param name           display name of the application
 * @param version        application version (filled in from pom.xml at build time)
 * @param allowedOrigins browser origins allowed to call the API (CORS)
 */
@Validated
@ConfigurationProperties(prefix = "civicconnect")
public record AppProperties(
        @NotBlank String name,
        @NotBlank String version,
        List<String> allowedOrigins) {

    public AppProperties {
        allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
    }
}
