package com.civicconnect.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Optional check against YOUR PostgreSQL server (not the embedded test server).
 * <p>
 * Runs only when the DB_URL environment variable is set, and is reported as skipped otherwise.
 * It only opens a connection and runs read-only queries; it never changes data or schema.
 */
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class LiveDatabaseConnectivityTest {

    @Test
    void canConnectToConfiguredPostgresServer() throws Exception {
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT current_database(), current_setting('server_version')")) {

            assertThat(connection.isValid(5)).isTrue();
            assertThat(result.next()).isTrue();
            System.out.printf("Connected to database '%s' on PostgreSQL %s%n", result.getString(1), result.getString(2));
        }
    }
}
