package com.civicconnect.entity;

import com.civicconnect.entity.enums.NotificationType;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;

/**
 * An in-app notification for a user, optionally about a specific complaint.
 */
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notifications_recipient"))
    private User recipient;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", foreignKey = @ForeignKey(name = "fk_notifications_complaint"))
    private Complaint complaint;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40)
    private NotificationType type;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank
    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "read_at")
    private Instant readAt;

    protected Notification() {
        // Required by JPA
    }

    public Notification(User recipient, Complaint complaint, NotificationType type, String title, String message) {
        this.recipient = recipient;
        this.complaint = complaint;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    /** Marks the notification as read, matching the {@code ck_notifications_read_state} constraint. */
    public void markAsRead(Instant readAt) {
        if (!read) {
            this.read = true;
            this.readAt = Objects.requireNonNull(readAt, "readAt");
        }
    }

    public User getRecipient() {
        return recipient;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public Instant getReadAt() {
        return readAt;
    }
}
