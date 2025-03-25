--liquibase formatted sql

--changeset mak.dz:1 dbms:postgresql
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_units_accommodation_type'
CREATE INDEX idx_units_accommodation_type ON units(accommodation_type);
--rollback DROP INDEX IF EXISTS idx_units_accommodation_type;

--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_units_floor'
CREATE INDEX idx_units_floor ON units(floor);
--rollback DROP INDEX IF EXISTS idx_units_floor;

--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_units_rooms_number'
CREATE INDEX idx_units_rooms_number ON units(rooms_number);
--rollback DROP INDEX IF EXISTS idx_units_rooms_number;

--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_bookings_date_range'
CREATE INDEX idx_bookings_date_range ON bookings(start_date, end_date);
--rollback DROP INDEX IF EXISTS idx_bookings_date_range;

--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_bookings_status'
CREATE INDEX idx_bookings_status ON bookings(status);
--rollback DROP INDEX IF EXISTS idx_bookings_status;

--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_bookings_unit_id'
CREATE INDEX idx_bookings_unit_id ON bookings(unit_id);
--rollback DROP INDEX IF EXISTS idx_bookings_unit_id;

--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace WHERE c.relname = 'idx_bookings_user_id'
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
--rollback DROP INDEX IF EXISTS idx_bookings_user_id;
