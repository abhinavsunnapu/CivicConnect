package com.civicconnect.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.civicconnect.entity.enums.ComplaintCategory;
import com.civicconnect.entity.enums.ComplaintPriority;
import com.civicconnect.entity.enums.ComplaintStatus;
import com.civicconnect.entity.enums.ImageType;
import com.civicconnect.entity.enums.NotificationType;
import com.civicconnect.entity.enums.UserRole;
import com.civicconnect.repository.ComplaintAssignmentRepository;
import com.civicconnect.repository.ComplaintRepository;
import com.civicconnect.repository.DepartmentRepository;
import com.civicconnect.repository.NotificationRepository;
import com.civicconnect.repository.UserRepository;
import com.civicconnect.support.AbstractPostgresIntegrationTest;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

/**
 * Persists and reloads the entity graph against the real (Flyway-migrated) PostgreSQL schema.
 * Hibernate's {@code ddl-auto=validate} also runs at startup, so a mismatch between entities and
 * migrations fails this test class.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EntityMappingTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository users;

    @Autowired
    private DepartmentRepository departments;

    @Autowired
    private ComplaintRepository complaints;

    @Autowired
    private ComplaintAssignmentRepository assignments;

    @Autowired
    private NotificationRepository notifications;

    @Test
    void persistsAndReloadsFullComplaintGraph() {
        Department roads = departments.findByCode("ROADS").orElseThrow();
        User citizen = em.persist(new User("Asha Citizen", "asha@example.com", "hash"));
        User officer = new User("Ravi Officer", "ravi@example.com", "hash");
        officer.setRole(UserRole.AUTHORITY);
        officer.setDepartment(roads);
        em.persist(officer);

        Complaint complaint = new Complaint(citizen, "Deep pothole near bus stop", "About 30cm wide, causing accidents",
                ComplaintCategory.POTHOLE, new BigDecimal("12.971599"), new BigDecimal("77.594566"));
        complaint.setPriority(ComplaintPriority.HIGH);
        complaint.setDepartment(roads);

        ComplaintImage photo = new ComplaintImage("complaints/2026/photo-1.jpg", "image/jpeg", 245_000);
        photo.setAiResult(ComplaintCategory.POTHOLE, new BigDecimal("0.9312"), Instant.now());
        complaint.addImage(photo);
        complaint.addStatusHistory(new ComplaintStatusHistory(null, ComplaintStatus.SUBMITTED, citizen, "Reported"));
        complaint.addAssignment(new ComplaintAssignment(roads, officer, null));
        em.persist(complaint);
        em.persist(new Notification(citizen, complaint, NotificationType.COMPLAINT_SUBMITTED,
                "Complaint received", "We received your report."));
        em.flush();
        em.clear();

        Complaint reloaded = complaints.findById(complaint.getId()).orElseThrow();

        assertThat(reloaded.getId().version()).as("UUIDv7 primary key").isEqualTo(7);
        assertThat(reloaded.getReferenceNumber()).matches("CC-\\d{4}-\\d{6,}");
        assertThat(reloaded.getCreatedAt()).isNotNull();
        assertThat(reloaded.getUpdatedAt()).isNotNull();
        assertThat(reloaded.getStatus()).isEqualTo(ComplaintStatus.SUBMITTED);
        assertThat(reloaded.getPriority()).isEqualTo(ComplaintPriority.HIGH);
        assertThat(reloaded.getLatitude()).isEqualByComparingTo("12.971599");

        assertThat(Hibernate.isInitialized(reloaded.getReporter())).as("reporter is lazy").isFalse();
        assertThat(reloaded.getReporter().getEmail()).isEqualTo("asha@example.com");

        assertThat(reloaded.getImages()).singleElement().satisfies(image -> {
            assertThat(image.getImageType()).isEqualTo(ImageType.REPORT);
            assertThat(image.getAiDetectedCategory()).isEqualTo(ComplaintCategory.POTHOLE);
            assertThat(image.getAiConfidence()).isEqualByComparingTo("0.9312");
        });
        assertThat(reloaded.getStatusHistory()).singleElement()
                .satisfies(entry -> assertThat(entry.getToStatus()).isEqualTo(ComplaintStatus.SUBMITTED));
        assertThat(assignments.findByComplaintIdAndActiveTrue(reloaded.getId())).isPresent();
        assertThat(notifications.countByRecipientIdAndReadFalse(citizen.getId())).isEqualTo(1);
    }

    @Test
    void normalisesEmailToLowerCase() {
        em.persistAndFlush(new User("Mixed Case", "  Mixed.Case@Example.COM ", "hash"));
        em.clear();

        assertThat(users.findByEmailNormalized("MIXED.case@example.com"))
                .get()
                .extracting(User::getEmail)
                .isEqualTo("mixed.case@example.com");
    }

    @Test
    void removingImageFromComplaintDeletesIt() {
        Complaint complaint = persistComplaint("orphan@example.com");
        ComplaintImage image = new ComplaintImage("complaints/2026/orphan.jpg", "image/png", 512);
        complaint.addImage(image);
        em.flush();

        complaint.removeImage(image);
        em.flush();
        em.clear();

        assertThat(em.find(ComplaintImage.class, image.getId())).isNull();
    }

    @Test
    void optimisticLockVersionIncrementsOnUpdate() {
        Complaint complaint = persistComplaint("version@example.com");
        long initialVersion = complaint.getVersion();

        complaint.setStatus(ComplaintStatus.UNDER_REVIEW);
        em.flush();

        assertThat(complaint.getVersion()).isEqualTo(initialVersion + 1);
    }

    @Test
    void endingAssignmentAndReadingNotificationSatisfyDatabaseConstraints() {
        Complaint complaint = persistComplaint("state@example.com");
        Department roads = departments.findByCode("ROADS").orElseThrow();
        ComplaintAssignment first = new ComplaintAssignment(roads, null, null);
        complaint.addAssignment(first);
        em.flush();

        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        first.end(now);
        em.flush();
        complaint.addAssignment(new ComplaintAssignment(roads, null, null));

        Notification notification = em.persist(new Notification(complaint.getReporter(), complaint,
                NotificationType.STATUS_UPDATED, "Status updated", "Your complaint is under review."));
        notification.markAsRead(now);
        em.flush();

        assertThat(assignments.findByComplaintIdAndActiveTrue(complaint.getId())).get()
                .isNotEqualTo(first);
        assertThat(notifications.countByRecipientIdAndReadFalse(complaint.getReporter().getId())).isZero();
    }

    @Test
    void beanValidationRejectsInvalidComplaint() {
        User reporter = em.persist(new User("Validator", "validator@example.com", "hash"));
        Complaint invalid = new Complaint(reporter, " ", "Description", ComplaintCategory.OTHER,
                new BigDecimal("120.0"), BigDecimal.ZERO);

        assertThatThrownBy(() -> em.persistAndFlush(invalid))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("title")
                .hasMessageContaining("latitude");
    }

    @Test
    void entitiesWithSameIdAreEqual() {
        Complaint complaint = persistComplaint("equality@example.com");
        em.clear();

        Complaint proxy = em.getEntityManager().getReference(Complaint.class, complaint.getId());

        assertThat(proxy).isEqualTo(complaint).hasSameHashCodeAs(complaint);
    }

    private Complaint persistComplaint(String reporterEmail) {
        User reporter = em.persist(new User("Reporter", reporterEmail, "hash"));
        Complaint complaint = new Complaint(reporter, "Overflowing bin", "Not collected for a week",
                ComplaintCategory.GARBAGE, new BigDecimal("28.613939"), new BigDecimal("77.209023"));
        return em.persistAndFlush(complaint);
    }
}
