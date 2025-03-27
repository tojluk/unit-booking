-- 2024-03-26-02-update-payments-table.sql
--liquibase formatted sql

--changeset mak.dz:1
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'payments' AND column_name = 'expiration_time'
ALTER TABLE payments ADD COLUMN expiration_time TIMESTAMP WITH TIME ZONE;
--rollback ALTER TABLE payments DROP COLUMN expiration_time;

--changeset mak.dz:2
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM pg_indexes WHERE indexname = 'idx_payments_expiration'
CREATE INDEX idx_payments_expiration ON payments(expiration_time);
--rollback DROP INDEX IF EXISTS idx_payments_expiration;

--changeset mak.dz:3
UPDATE payments
SET expiration_time = created_at + INTERVAL '15 minutes'
WHERE expiration_time IS NULL;
