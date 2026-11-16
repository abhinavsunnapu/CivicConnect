package com.civicconnect.entity.enums;

/**
 * Kind of in-app notification. Must match ck_notifications_type in the database.
 */
public enum NotificationType {
    COMPLAINT_SUBMITTED,
    STATUS_UPDATED,
    COMPLAINT_ASSIGNED,
    COMPLAINT_RESOLVED,
    GENERAL
}
