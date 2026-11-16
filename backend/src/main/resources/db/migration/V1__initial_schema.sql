-- =====================================================================
-- CivicConnect - V1: initial schema
--
-- Conventions
--   * UUID primary keys. The application generates time-ordered UUIDv7
--     values; gen_random_uuid() is a fallback for rows inserted by hand.
--   * Enumerations are VARCHAR + CHECK constraints (not native ENUM types),
--     so new values can be added with a simple constraint change.
--   * All timestamps are TIMESTAMPTZ (stored in UTC).
--   * Every foreign-key column is indexed (PostgreSQL does not do this
--     automatically).
-- =====================================================================

-- ---------------------------------------------------------------------
-- Human-readable complaint reference numbers, e.g. CC-2026-000042
-- ---------------------------------------------------------------------
CREATE SEQUENCE complaint_reference_seq START WITH 1 INCREMENT BY 1;

CREATE FUNCTION next_complaint_reference() RETURNS VARCHAR
    LANGUAGE sql
    VOLATILE
AS
$$
SELECT 'CC-' || to_char(now() AT TIME ZONE 'UTC', 'YYYY') || '-'
           || lpad(seq.n::text, greatest(6, length(seq.n::text)), '0')
FROM (SELECT nextval('complaint_reference_seq') AS n) AS seq;
$$;

-- ---------------------------------------------------------------------
-- departments: municipal departments that handle complaints
-- ---------------------------------------------------------------------
CREATE TABLE departments
(
    id            UUID         NOT NULL DEFAULT gen_random_uuid(),
    code          VARCHAR(30)  NOT NULL,
    name          VARCHAR(150) NOT NULL,
    description   TEXT,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_departments PRIMARY KEY (id),
    CONSTRAINT uk_departments_code UNIQUE (code),
    CONSTRAINT uk_departments_name UNIQUE (name),
    CONSTRAINT ck_departments_code_format CHECK (code ~ '^[A-Z][A-Z0-9_]*$'),
    CONSTRAINT ck_departments_name_not_blank CHECK (btrim(name) <> '')
);

-- ---------------------------------------------------------------------
-- users: citizens, department staff (AUTHORITY) and administrators
-- ---------------------------------------------------------------------
CREATE TABLE users
(
    id            UUID         NOT NULL DEFAULT gen_random_uuid(),
    full_name     VARCHAR(150) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'CITIZEN',
    department_id UUID,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_phone_number UNIQUE (phone_number),
    CONSTRAINT fk_users_department FOREIGN KEY (department_id)
        REFERENCES departments (id) ON DELETE SET NULL,
    CONSTRAINT ck_users_role CHECK (role IN ('CITIZEN', 'AUTHORITY', 'ADMIN')),
    -- Emails are stored lower-case, which makes uk_users_email case-insensitive.
    CONSTRAINT ck_users_email_lowercase CHECK (email = lower(email)),
    CONSTRAINT ck_users_full_name_not_blank CHECK (btrim(full_name) <> '')
);

CREATE INDEX idx_users_department_id ON users (department_id);

-- ---------------------------------------------------------------------
-- complaints: a civic issue reported by a citizen
-- ---------------------------------------------------------------------
CREATE TABLE complaints
(
    id               UUID          NOT NULL DEFAULT gen_random_uuid(),
    reference_number VARCHAR(20)   NOT NULL DEFAULT next_complaint_reference(),
    reporter_id      UUID          NOT NULL,
    department_id    UUID,
    title            VARCHAR(200)  NOT NULL,
    description      TEXT          NOT NULL,
    category         VARCHAR(30)   NOT NULL,
    status           VARCHAR(30)   NOT NULL DEFAULT 'SUBMITTED',
    priority         VARCHAR(20)   NOT NULL DEFAULT 'MEDIUM',
    latitude         NUMERIC(9, 6) NOT NULL,
    longitude        NUMERIC(9, 6) NOT NULL,
    address          VARCHAR(500),
    resolved_at      TIMESTAMPTZ,
    version          BIGINT        NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_complaints PRIMARY KEY (id),
    CONSTRAINT uk_complaints_reference_number UNIQUE (reference_number),
    CONSTRAINT fk_complaints_reporter FOREIGN KEY (reporter_id)
        REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_complaints_department FOREIGN KEY (department_id)
        REFERENCES departments (id) ON DELETE SET NULL,
    CONSTRAINT ck_complaints_category CHECK (category IN
        ('POTHOLE', 'GARBAGE', 'WATER_LEAKAGE', 'STREETLIGHT', 'ROAD_DAMAGE', 'DRAINAGE', 'OTHER')),
    CONSTRAINT ck_complaints_status CHECK (status IN
        ('SUBMITTED', 'UNDER_REVIEW', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'REJECTED', 'REOPENED')),
    CONSTRAINT ck_complaints_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT ck_complaints_latitude CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT ck_complaints_longitude CHECK (longitude BETWEEN -180 AND 180),
    CONSTRAINT ck_complaints_title_not_blank CHECK (btrim(title) <> ''),
    CONSTRAINT ck_complaints_version_non_negative CHECK (version >= 0)
);

CREATE INDEX idx_complaints_reporter_id ON complaints (reporter_id);
CREATE INDEX idx_complaints_department_id ON complaints (department_id);
CREATE INDEX idx_complaints_status_created_at ON complaints (status, created_at DESC);
CREATE INDEX idx_complaints_category ON complaints (category);
CREATE INDEX idx_complaints_created_at ON complaints (created_at DESC);
-- Supports bounding-box queries for the map view.
CREATE INDEX idx_complaints_location ON complaints (latitude, longitude);

-- ---------------------------------------------------------------------
-- complaint_images: photos attached to a complaint, plus AI results
-- ---------------------------------------------------------------------
CREATE TABLE complaint_images
(
    id                   UUID          NOT NULL DEFAULT gen_random_uuid(),
    complaint_id         UUID          NOT NULL,
    image_type           VARCHAR(20)   NOT NULL DEFAULT 'REPORT',
    storage_path         VARCHAR(500)  NOT NULL,
    original_filename    VARCHAR(255),
    content_type         VARCHAR(100)  NOT NULL,
    file_size_bytes      BIGINT        NOT NULL,
    ai_detected_category VARCHAR(30),
    ai_confidence        NUMERIC(5, 4),
    ai_processed_at      TIMESTAMPTZ,
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_complaint_images PRIMARY KEY (id),
    CONSTRAINT uk_complaint_images_storage_path UNIQUE (storage_path),
    CONSTRAINT fk_complaint_images_complaint FOREIGN KEY (complaint_id)
        REFERENCES complaints (id) ON DELETE CASCADE,
    CONSTRAINT ck_complaint_images_image_type CHECK (image_type IN ('REPORT', 'RESOLUTION')),
    CONSTRAINT ck_complaint_images_content_type CHECK (content_type LIKE 'image/%'),
    CONSTRAINT ck_complaint_images_file_size CHECK (file_size_bytes > 0),
    CONSTRAINT ck_complaint_images_ai_category CHECK (ai_detected_category IN
        ('POTHOLE', 'GARBAGE', 'WATER_LEAKAGE', 'STREETLIGHT', 'ROAD_DAMAGE', 'DRAINAGE', 'OTHER')),
    CONSTRAINT ck_complaint_images_ai_confidence CHECK (ai_confidence BETWEEN 0 AND 1),
    -- An AI prediction is recorded as a whole: category and confidence together, or neither.
    CONSTRAINT ck_complaint_images_ai_result_complete CHECK
        ((ai_detected_category IS NULL) = (ai_confidence IS NULL))
);

CREATE INDEX idx_complaint_images_complaint_id ON complaint_images (complaint_id);

-- ---------------------------------------------------------------------
-- complaint_status_history: append-only audit trail of status changes
-- ---------------------------------------------------------------------
CREATE TABLE complaint_status_history
(
    id            UUID        NOT NULL DEFAULT gen_random_uuid(),
    complaint_id  UUID        NOT NULL,
    from_status   VARCHAR(30),
    to_status     VARCHAR(30) NOT NULL,
    changed_by_id UUID,
    remarks       TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_complaint_status_history PRIMARY KEY (id),
    CONSTRAINT fk_complaint_status_history_complaint FOREIGN KEY (complaint_id)
        REFERENCES complaints (id) ON DELETE CASCADE,
    -- NULL changed_by_id means the change was made by the system.
    CONSTRAINT fk_complaint_status_history_changed_by FOREIGN KEY (changed_by_id)
        REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT ck_complaint_status_history_from_status CHECK (from_status IN
        ('SUBMITTED', 'UNDER_REVIEW', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'REJECTED', 'REOPENED')),
    CONSTRAINT ck_complaint_status_history_to_status CHECK (to_status IN
        ('SUBMITTED', 'UNDER_REVIEW', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'REJECTED', 'REOPENED')),
    CONSTRAINT ck_complaint_status_history_changed CHECK (from_status IS NULL OR from_status <> to_status)
);

CREATE INDEX idx_complaint_status_history_complaint_id ON complaint_status_history (complaint_id, created_at);
CREATE INDEX idx_complaint_status_history_changed_by_id ON complaint_status_history (changed_by_id);

-- ---------------------------------------------------------------------
-- complaint_assignments: which department / staff member handles a complaint
-- ---------------------------------------------------------------------
CREATE TABLE complaint_assignments
(
    id             UUID        NOT NULL DEFAULT gen_random_uuid(),
    complaint_id   UUID        NOT NULL,
    department_id  UUID        NOT NULL,
    assigned_to_id UUID,
    assigned_by_id UUID,
    notes          TEXT,
    due_at         TIMESTAMPTZ,
    is_active      BOOLEAN     NOT NULL DEFAULT TRUE,
    unassigned_at  TIMESTAMPTZ,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_complaint_assignments PRIMARY KEY (id),
    CONSTRAINT fk_complaint_assignments_complaint FOREIGN KEY (complaint_id)
        REFERENCES complaints (id) ON DELETE CASCADE,
    CONSTRAINT fk_complaint_assignments_department FOREIGN KEY (department_id)
        REFERENCES departments (id) ON DELETE RESTRICT,
    CONSTRAINT fk_complaint_assignments_assigned_to FOREIGN KEY (assigned_to_id)
        REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_complaint_assignments_assigned_by FOREIGN KEY (assigned_by_id)
        REFERENCES users (id) ON DELETE SET NULL,
    -- Active assignments have no end time; ended assignments must record one.
    CONSTRAINT ck_complaint_assignments_active_state CHECK
        ((is_active AND unassigned_at IS NULL) OR (NOT is_active AND unassigned_at IS NOT NULL))
);

-- At most one active assignment per complaint; past assignments are kept as history.
CREATE UNIQUE INDEX uk_complaint_assignments_one_active
    ON complaint_assignments (complaint_id) WHERE is_active;
CREATE INDEX idx_complaint_assignments_complaint_id ON complaint_assignments (complaint_id);
CREATE INDEX idx_complaint_assignments_department_id ON complaint_assignments (department_id);
CREATE INDEX idx_complaint_assignments_assigned_to_id ON complaint_assignments (assigned_to_id);
CREATE INDEX idx_complaint_assignments_assigned_by_id ON complaint_assignments (assigned_by_id);

-- ---------------------------------------------------------------------
-- notifications: in-app messages to users
-- ---------------------------------------------------------------------
CREATE TABLE notifications
(
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    recipient_id UUID         NOT NULL,
    complaint_id UUID,
    type         VARCHAR(40)  NOT NULL,
    title        VARCHAR(200) NOT NULL,
    message      TEXT         NOT NULL,
    is_read      BOOLEAN      NOT NULL DEFAULT FALSE,
    read_at      TIMESTAMPTZ,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_complaint FOREIGN KEY (complaint_id)
        REFERENCES complaints (id) ON DELETE CASCADE,
    CONSTRAINT ck_notifications_type CHECK (type IN
        ('COMPLAINT_SUBMITTED', 'STATUS_UPDATED', 'COMPLAINT_ASSIGNED', 'COMPLAINT_RESOLVED', 'GENERAL')),
    CONSTRAINT ck_notifications_read_state CHECK (is_read = (read_at IS NOT NULL))
);

CREATE INDEX idx_notifications_recipient_created_at ON notifications (recipient_id, created_at DESC);
CREATE INDEX idx_notifications_recipient_unread ON notifications (recipient_id) WHERE NOT is_read;
CREATE INDEX idx_notifications_complaint_id ON notifications (complaint_id);

-- ---------------------------------------------------------------------
-- Documentation
-- ---------------------------------------------------------------------
COMMENT ON TABLE departments IS 'Municipal departments that handle complaints';
COMMENT ON TABLE users IS 'Platform users: citizens, department staff (AUTHORITY) and administrators';
COMMENT ON TABLE complaints IS 'Civic issues reported by citizens';
COMMENT ON TABLE complaint_images IS 'Photos attached to complaints, with optional AI detection results';
COMMENT ON TABLE complaint_status_history IS 'Append-only audit trail of complaint status changes';
COMMENT ON TABLE complaint_assignments IS 'Department and staff assignments for complaints (current and past)';
COMMENT ON TABLE notifications IS 'In-app notifications sent to users';
COMMENT ON COLUMN complaints.department_id IS 'Department the complaint is currently routed to';
COMMENT ON COLUMN complaints.version IS 'Optimistic-locking version, managed by JPA';
