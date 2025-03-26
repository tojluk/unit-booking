--liquibase formatted sql

--changeset mak.dzehtsiarou:1 dbms:postgresql context:initial-schema
CREATE SEQUENCE IF NOT EXISTS units_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS bookings_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS payments_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS events_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS units (
    id BIGINT DEFAULT nextval('units_seq') PRIMARY KEY,
    rooms_number INTEGER NOT NULL,
    accommodation_type accommodation_type NOT NULL,
    floor INTEGER NOT NULL,
    base_cost DECIMAL(10,2) NOT NULL,
    description TEXT,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT rooms_number_positive CHECK (rooms_number > 0),
    CONSTRAINT floor_positive CHECK (floor >= 0),
    CONSTRAINT base_cost_positive CHECK (base_cost > 0)
    );


CREATE TABLE IF NOT EXISTS users (
    id BIGINT DEFAULT nextval('users_seq') PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT email_valid CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
    );

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT DEFAULT nextval('bookings_seq') PRIMARY KEY,
    unit_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status booking_status NOT NULL DEFAULT 'PENDING',
    total_cost DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_unit FOREIGN KEY (unit_id) REFERENCES units(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT dates_valid CHECK (end_date >= start_date),
    CONSTRAINT total_cost_positive CHECK (total_cost > 0)
    );


CREATE TABLE IF NOT EXISTS payments (
    id BIGINT DEFAULT nextval('payments_seq') PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status payment_status NOT NULL DEFAULT 'PENDING',
    payment_date TIMESTAMP WITH TIME ZONE,
    expiration_date TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT amount_positive CHECK (amount > 0)
    );


CREATE TABLE IF NOT EXISTS events (
    id BIGINT DEFAULT nextval('events_seq') PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    event_type event_type NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
