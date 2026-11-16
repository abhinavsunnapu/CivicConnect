package com.civicconnect.entity.enums;

/**
 * How urgently a complaint should be handled. Must match ck_complaints_priority in the database.
 */
public enum ComplaintPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
