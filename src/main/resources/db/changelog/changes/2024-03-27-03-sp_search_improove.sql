-- 2024-03-27-02-add_random_units.sql
--liquibase formatted sql

--changeset mak.dz:1
--preconditions onFail:MARK_RAN
CREATE INDEX idx_units_created_at_desc ON units(created_at DESC);
CREATE INDEX idx_bookings_active ON bookings(unit_id, start_date, end_date)
    WHERE status IN ('PENDING', 'CONFIRMED');
