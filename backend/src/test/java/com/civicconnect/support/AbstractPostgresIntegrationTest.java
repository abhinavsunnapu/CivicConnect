package com.civicconnect.support;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class for tests that need a database.
 * <p>
 * Starts one real PostgreSQL server (embedded binaries, no Docker or local install) per test run and points
 * Spring's datasource at it. Tests never touch the developer's own database, even when DB_URL is set.
 * Flyway runs the real migrations against this server when the Spring context starts.
 */
public abstract class AbstractPostgresIntegrationTest {

    private static final String USERNAME = "postgres";
    private static final String DATABASE = "postgres";

    private static EmbeddedPostgres postgres;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        EmbeddedPostgres server = server();
        registry.add("spring.datasource.url", () -> server.getJdbcUrl(USERNAME, DATABASE));
        registry.add("spring.datasource.username", () -> USERNAME);
        registry.add("spring.datasource.password", () -> "");
    }

    private static synchronized EmbeddedPostgres server() {
        if (postgres == null) {
            try {
                postgres = EmbeddedPostgres.builder().start();
            } catch (IOException e) {
                throw new UncheckedIOException("Could not start embedded PostgreSQL for tests", e);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    postgres.close();
                } catch (IOException ignored) {
                    // JVM is exiting; nothing more to do
                }
            }, "embedded-postgres-shutdown"));
        }
        return postgres;
    }
}
