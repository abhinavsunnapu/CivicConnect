-- =====================================================================
-- CivicConnect - local development database setup
--
-- Run once as the PostgreSQL superuser:
--   psql -U postgres -h localhost -f database/scripts/create_local_database.sql
--
-- Replace CHANGE_ME with your own password before running.
-- Do NOT commit a real password to version control.
--
-- This script only creates the role and the empty database.
-- The tables are created by the backend's Flyway migrations on startup.
-- =====================================================================

CREATE ROLE civicconnect WITH LOGIN PASSWORD 'CHANGE_ME';

CREATE DATABASE civicconnect
    OWNER civicconnect
    ENCODING 'UTF8'
    TEMPLATE template0;

\connect civicconnect

-- PostgreSQL 15+ no longer lets every user create objects in "public".
-- Hand the schema to the application role so Flyway can create tables.
ALTER SCHEMA public OWNER TO civicconnect;
