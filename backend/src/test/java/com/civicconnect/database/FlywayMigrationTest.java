package com.civicconnect.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.civicconnect.support.AbstractPostgresIntegrationTest;
import java.util.List;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verifies the Flyway migrations against a real PostgreSQL server: they apply cleanly, create the
 * expected tables, and the database enforces the constraints on its own.
 * Each test runs in a transaction that is rolled back.
 */
@SpringBootTest
@Transactional
class FlywayMigrationTest extends AbstractPostgresIntegrationTest {

    private static final List<String> EXPECTED_TABLES = List.of(
            "users", "departments", "complaints", "complaint_images",
            "complaint_status_history", "complaint_assignments", "notifications");

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void allMigrationsAreAppliedSuccessfully() {
        MigrationInfo[] applied = flyway.info().applied();

        assertThat(applied).extracting(info -> info.getVersion().getVersion()).containsExactly("1", "2");
        assertThat(applied).allSatisfy(info -> assertThat(info.getState()).isEqualTo(MigrationState.SUCCESS));
        assertThat(flyway.info().pending()).isEmpty();
    }

    @Test
    void allSevenTablesExist() {
        List<String> tables = jdbc.queryForList("""
                SELECT table_name FROM information_schema.tables
                WHERE table_schema = 'public' AND table_type = 'BASE TABLE'
                """, String.class);

        assertThat(tables).containsAll(EXPECTED_TABLES);
    }

    @Test
    void primaryKeysAreUuids() {
        List<String> idTypes = jdbc.queryForList("""
                SELECT data_type FROM information_schema.columns
                WHERE table_schema = 'public' AND column_name = 'id' AND table_name = ANY (?)
                """, String.class, (Object) EXPECTED_TABLES.toArray(String[]::new));

        assertThat(idTypes).hasSize(EXPECTED_TABLES.size()).containsOnly("uuid");
    }

    @Test
    void defaultDepartmentsAreSeeded() {
        List<String> codes = jdbc.queryForList("SELECT code FROM departments ORDER BY code", String.class);

        assertThat(codes).contains("DRAINAGE", "ELECTRICAL", "GENERAL", "ROADS", "SANITATION", "WATER_SUPPLY");
    }

    @Test
    void complaintGetsReferenceNumberAndDefaultsFromDatabase() {
        UUID reporterId = insertUser("ref@example.com");

        String reference = jdbc.queryForObject("""
                INSERT INTO complaints (reporter_id, title, description, category, latitude, longitude)
                VALUES (?, 'Pothole', 'Large pothole', 'POTHOLE', 12.971599, 77.594566)
                RETURNING reference_number || '|' || status || '|' || priority
                """, String.class, reporterId);

        assertThat(reference).matches("CC-\\d{4}-\\d{6,}\\|SUBMITTED\\|MEDIUM");
    }

    @Test
    void rejectsUnknownComplaintStatus() {
        UUID reporterId = insertUser("status@example.com");

        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO complaints (reporter_id, title, description, category, status, latitude, longitude)
                VALUES (?, 'Title', 'Description', 'POTHOLE', 'CLOSED', 0, 0)
                """, reporterId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("ck_complaints_status");
    }

    @Test
    void rejectsOutOfRangeCoordinates() {
        UUID reporterId = insertUser("coords@example.com");

        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO complaints (reporter_id, title, description, category, latitude, longitude)
                VALUES (?, 'Title', 'Description', 'GARBAGE', 95, 0)
                """, reporterId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("ck_complaints_latitude");
    }

    @Test
    void rejectsDuplicateEmail() {
        insertUser("duplicate@example.com");

        assertThatThrownBy(() -> insertUser("duplicate@example.com"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("uk_users_email");
    }

    @Test
    void rejectsMixedCaseEmail() {
        assertThatThrownBy(() -> insertUser("Mixed@Example.com"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("ck_users_email_lowercase");
    }

    @Test
    void allowsOnlyOneActiveAssignmentPerComplaint() {
        UUID complaintId = insertComplaint(insertUser("assign@example.com"));
        UUID departmentId = jdbc.queryForObject("SELECT id FROM departments WHERE code = 'ROADS'", UUID.class);
        String insertAssignment = "INSERT INTO complaint_assignments (complaint_id, department_id) VALUES (?, ?)";

        jdbc.update(insertAssignment, complaintId, departmentId);

        assertThatThrownBy(() -> jdbc.update(insertAssignment, complaintId, departmentId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("uk_complaint_assignments_one_active");
    }

    @Test
    void deletingComplaintCascadesToImages() {
        UUID complaintId = insertComplaint(insertUser("cascade@example.com"));
        jdbc.update("""
                INSERT INTO complaint_images (complaint_id, storage_path, content_type, file_size_bytes)
                VALUES (?, 'complaints/test/photo.jpg', 'image/jpeg', 1024)
                """, complaintId);

        jdbc.update("DELETE FROM complaints WHERE id = ?", complaintId);

        Integer remaining = jdbc.queryForObject(
                "SELECT count(*) FROM complaint_images WHERE complaint_id = ?", Integer.class, complaintId);
        assertThat(remaining).isZero();
    }

    @Test
    void cannotDeleteUserWhoReportedComplaints() {
        UUID reporterId = insertUser("restrict@example.com");
        insertComplaint(reporterId);

        assertThatThrownBy(() -> jdbc.update("DELETE FROM users WHERE id = ?", reporterId))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("fk_complaints_reporter");
    }

    private UUID insertUser(String email) {
        return jdbc.queryForObject("""
                INSERT INTO users (full_name, email, password_hash)
                VALUES ('Test User', ?, 'not-a-real-hash')
                RETURNING id
                """, UUID.class, email);
    }

    private UUID insertComplaint(UUID reporterId) {
        return jdbc.queryForObject("""
                INSERT INTO complaints (reporter_id, title, description, category, latitude, longitude)
                VALUES (?, 'Title', 'Description', 'DRAINAGE', 19.076090, 72.877426)
                RETURNING id
                """, UUID.class, reporterId);
    }
}
