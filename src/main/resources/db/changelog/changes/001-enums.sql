--liquibase formatted sql

--changeset mak.dz:1 dbms:postgresql context:enums

--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_type WHERE typname = 'accommodation_type'
CREATE TYPE accommodation_type AS ENUM ('HOME', 'FLAT', 'APARTMENTS');
--rollback DROP TYPE IF EXISTS accommodation_type;

--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_type WHERE typname = 'booking_status'
CREATE TYPE booking_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED');
--rollback DROP TYPE IF EXISTS booking_status;

--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_type WHERE typname = 'payment_status'
CREATE TYPE payment_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED', 'EXPIRED');
--rollback DROP TYPE IF EXISTS payment_status;

--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_type WHERE typname = 'event_type'
CREATE TYPE event_type AS ENUM ('CREATED', 'UPDATED', 'DELETED', 'BOOKED', 'CANCELLED');
--rollback DROP TYPE IF EXISTS event_type;
