# CivicConnect Backend

The REST API for CivicConnect, built with **Java 21**, **Spring Boot 4.1** and **Maven**.

> **Status:** Health-check endpoint, plus the PostgreSQL schema (Flyway migrations, JPA entities and repositories). Authentication and complaint APIs will be added in later steps.

## Requirements

| Tool | Version | Notes |
|---|---|---|
| JDK | 21 or newer | The code is compiled to Java 21 (`<java.version>21</java.version>`) |
| Maven | Not needed | Use the included Maven Wrapper (`mvnw` / `mvnw.cmd`) |
| PostgreSQL | 13 or newer (18 recommended) | Needed to **run** the app. Tests don't need it (see [Tests](#run-the-tests)) |

Check your Java version:

```bash
java -version
```

## Database setup

1. Install PostgreSQL and create the database and user. See [`../database/README.md`](../database/README.md) for step-by-step instructions.
2. Give the backend the connection details through environment variables:

   | Variable | Example | Required |
   |---|---|---|
   | `DB_URL` | `jdbc:postgresql://localhost:5432/civicconnect` | No (this is the default) |
   | `DB_USERNAME` | `civicconnect` | Yes |
   | `DB_PASSWORD` | your password | Yes |

   For local development, the easiest option is a `.env` file:

   ```bash
   cp .env.example .env    # then edit .env with your password
   ```

   `.env` is git-ignored and is read automatically when you start the app from `backend/`. Real environment variables take precedence over it.

3. Start the app. **Flyway** applies any pending migrations from `src/main/resources/db/migration`, then **Hibernate validates** that the entities match the schema (`ddl-auto: validate`). Hibernate never creates or alters tables.

### Changing the schema

Never edit a migration that has already been applied. Add a new file instead, for example `V3__add_complaint_upvotes.sql`. Flyway refuses to start if an applied migration has been changed.

## Running the application

From the `backend/` directory:

```bash
# macOS / Linux / Git Bash
./mvnw spring-boot:run

# Windows (Command Prompt / PowerShell)
mvnw.cmd spring-boot:run
```

The API starts at **http://localhost:8080**.

### Build and run the jar

```bash
./mvnw clean package              # runs tests, builds target/civicconnect-backend.jar
java -jar target/civicconnect-backend.jar
```

### Run the tests

```bash
./mvnw test
```

Database tests run against a **real PostgreSQL server that the test suite starts by itself** (Zonky embedded-postgres, no Docker or local install needed). The first run downloads the PostgreSQL binaries from Maven Central. Tests never touch your own database.

| Test class | What it checks |
|---|---|
| `FlywayMigrationTest` | Migrations apply cleanly; tables, UUID keys, seed data, and the CHECK, UNIQUE and foreign-key constraints |
| `EntityMappingTest` | Entities save and reload correctly, lazy loading, cascades, optimistic locking, Bean Validation |
| `EntityJsonSerializationTest` | Entities don't recurse in JSON and never expose `passwordHash` |
| `LiveDatabaseConnectivityTest` | **Optional**: connects to *your* server, read-only. Runs only when the `DB_URL` **environment variable** is set (a `.env` file doesn't count); otherwise skipped |

## Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/health` | Application health check |
| `GET` | `/actuator/health` | Spring Boot Actuator health (for monitoring tools) |
| `GET` | `/actuator/info` | Application name and version |

Example:

```bash
curl http://localhost:8080/api/v1/health
```

```json
{
  "status": "UP",
  "application": "CivicConnect",
  "version": "1.0.0"
}
```

Errors are returned as JSON in this shape:

```json
{
  "timestamp": "2026-01-01T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/v1/unknown",
  "fieldErrors": {}
}
```

## Configuration

Settings live in [`src/main/resources/application.yml`](src/main/resources/application.yml).

| Property | Default | Description |
|---|---|---|
| `server.port` | `8080` | HTTP port |
| `spring.datasource.url` | `${DB_URL}` | JDBC URL (default `jdbc:postgresql://localhost:5432/civicconnect`) |
| `spring.datasource.username` / `password` | `${DB_USERNAME}` / `${DB_PASSWORD}` | Database credentials (no defaults) |
| `spring.jpa.hibernate.ddl-auto` | `validate` | Hibernate checks the schema but never changes it |
| `spring.application.name` | `civicconnect-backend` | Service name (appears in logs) |
| `civicconnect.name` | `CivicConnect` | Name returned by the health endpoint |
| `civicconnect.version` | from `pom.xml` | Version returned by the health endpoint |
| `civicconnect.allowed-origins` | `http://localhost:5173` | Browser origins allowed to call `/api/**` (CORS) |

Any property can be overridden at startup, for example:

```bash
java -jar target/civicconnect-backend.jar --server.port=9090
# or with an environment variable
SERVER_PORT=9090 java -jar target/civicconnect-backend.jar
```

## Project structure

```
src/main/java/com/civicconnect/
├── CivicConnectApplication.java   # Application entry point
├── config/        # Configuration classes and typed properties (AppProperties, CORS)
├── controller/    # REST controllers (HTTP layer)
├── service/       # Business logic
├── repository/    # Spring Data JPA repositories
├── entity/        # JPA entities (BaseEntity + 7 tables); enums/ holds status, category, etc.
├── dto/           # Request/response objects returned by the API
└── exception/     # Global exception handling and custom exceptions
```

Requests flow **controller → service → repository**. Controllers return DTOs, never entities.
