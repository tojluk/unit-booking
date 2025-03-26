-- 2024-03-20-01-update-units-table.sql
--liquibase formatted sql

--changeset mak.dz:1
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'units' AND column_name = 'markup_percentage'
ALTER TABLE units ADD COLUMN markup_percentage DECIMAL(5,2) DEFAULT 15.00;
--rollback ALTER TABLE units DROP COLUMN markup_percentage;

--changeset mak.dz:2
UPDATE units
SET markup_percentage = 15.00
WHERE markup_percentage IS NULL;
