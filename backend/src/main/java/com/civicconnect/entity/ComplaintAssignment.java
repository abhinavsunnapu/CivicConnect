package com.civicconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

/**
 * Assignment of a complaint to a department and, optionally, a specific staff member.
 * <p>
 * A complaint has at most one active assignment (enforced by a partial unique index).
 * Reassigning ends the current assignment with {@link #end(Instant)} and adds a new one, so history is kept.
 */
@Entity
@Table(name = "complaint_assignments")
public class ComplaintAssignment extends BaseEntity {

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_complaint_assignments_complaint"))
    private Complaint complaint;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_complaint_assignments_department"))
    private Department department;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", foreignKey = @ForeignKey(name = "fk_complaint_assignments_assigned_to"))
    private User assignedTo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_id", foreignKey = @ForeignKey(name = "fk_complaint_assignments_assigned_by"))
    private User assignedBy;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "due_at")
    private Instant dueAt;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "unassigned_at")
    private Instant unassignedAt;

    protected ComplaintAssignment() {
        // Required by JPA
    }

    public ComplaintAssignment(Department department, User assignedTo, User assignedBy) {
        this.department = department;
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
    }

    /** Ends this assignment, matching the {@code ck_complaint_assignments_active_state} constraint. */
    public void end(Instant endedAt) {
        this.active = false;
        this.unassignedAt = Objects.requireNonNull(endedAt, "endedAt");
    }

    public Complaint getComplaint() {
        return complaint;
    }

    /** Package-private: use {@link Complaint#addAssignment} so both sides stay in sync. */
    void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public Department getDepartment() {
        return department;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getUnassignedAt() {
        return unassignedAt;
    }
}
