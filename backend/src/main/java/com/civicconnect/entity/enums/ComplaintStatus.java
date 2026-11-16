package com.civicconnect.entity.enums;

/**
 * Lifecycle status of a complaint. Must match ck_complaints_status in the database.
 */
public enum ComplaintStatus {
    SUBMITTED,
    UNDER_REVIEW,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    REJECTED,
    REOPENED
}
