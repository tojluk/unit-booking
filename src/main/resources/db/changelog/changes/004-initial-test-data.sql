--liquibase formatted sql

--changeset mak.dz:1 dbms:postgresql context:test-data
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM users
INSERT INTO users (username, email)
VALUES
    ('test_user', 'test.user@example.com');

--changeset mak.dz:2 dbms:postgresql context:test-data
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM units
INSERT INTO units (rooms_number, accommodation_type, floor, base_cost, description)
VALUES
    (2, 'FLAT', 3, 100.00, 'Modern apartment with park view. Fully furnished with household appliances'),
    (3, 'APARTMENTS', 5, 150.00, 'Spacious apartments with panoramic windows. Separate kitchen-dining area'),
    (1, 'FLAT', 2, 80.00, 'Cozy studio in the historic center. Ideal for a single person or couple'),
    (4, 'HOME', 1, 200.00, 'Private house with garden. Two-car garage, barbecue area'),
    (2, 'APARTMENTS', 4, 120.00, 'Bright apartments with modern renovation. Two bathrooms'),
    (3, 'HOME', 2, 180.00, 'Two-story townhouse with terrace. Gated community'),
    (1, 'FLAT', 7, 90.00, 'Compact apartment with high ceilings. Built-in kitchen'),
    (2, 'FLAT', 3, 110.00, 'Apartment with separate rooms. Fresh renovation, air conditioning'),
    (5, 'APARTMENTS', 6, 250.00, 'Penthouse with terrace. Panoramic city view'),
    (2, 'HOME', 1, 160.00, 'Small house with land plot. Autonomous heating');

--changeset mak.dz:3 dbms:postgresql context:test-data
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM events
INSERT INTO events (entity_type, entity_id, event_type, description)
VALUES
    ('UNIT', 1, 'CREATED', 'Unit creation'),
    ('UNIT', 2, 'CREATED', 'Unit creation'),
    ('UNIT', 3, 'CREATED', 'Unit creation'),
    ('UNIT', 4, 'CREATED', 'Unit creation'),
    ('UNIT', 5, 'CREATED', 'Unit creation'),
    ('UNIT', 6, 'CREATED', 'Unit creation'),
    ('UNIT', 7, 'CREATED', 'Unit creation'),
    ('UNIT', 8, 'CREATED', 'Unit creation'),
    ('UNIT', 9, 'CREATED', 'Unit creation'),
    ('UNIT', 10, 'CREATED', 'Unit creation');
