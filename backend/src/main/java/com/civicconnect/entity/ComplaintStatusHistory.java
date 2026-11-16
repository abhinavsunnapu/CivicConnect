package com.civicconnect.entity;

import com.civicconnect.entity.enums.ComplaintStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Immutable;

/**
 * One entry in a complaint's audit trail. Entries are append-only: Hibernate never updates them.
 */
@Entity
@Immutable
@Table(name = "complaint_status_history")
public class ComplaintStatusHistory extends BaseEntity {

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_complaint_status_history_complaint"))
    private Complaint complaint;

    /** Previous status; {@code null} for the first entry when the complaint is created. */
    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30, updatable = false)
    private ComplaintStatus fromStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30, updatable = false)
    private ComplaintStatus toStatus;

    /** Who made the change; {@code null} means the system did. */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id", updatable = false,
            foreignKey = @ForeignKey(name = "fk_complaint_status_history_changed_by"))
    private User changedBy;

    @Column(name = "remarks", columnDefinition = "text", updatable = false)
    private String remarks;

    protected ComplaintStatusHistory() {
        // Required by JPA
    }

    public ComplaintStatusHistory(ComplaintStatus fromStatus, ComplaintStatus toStatus, User changedBy, String remarks) {
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.remarks = remarks;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    /** Package-private: use {@link Complaint#addStatusHistory} so both sides stay in sync. */
    void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public ComplaintStatus getFromStatus() {
        return fromStatus;
    }

    public ComplaintStatus getToStatus() {
        return toStatus;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public String getRemarks() {
        return remarks;
    }
}
