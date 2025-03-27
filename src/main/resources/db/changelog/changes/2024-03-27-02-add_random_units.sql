-- 2024-03-27-02-add_random_units.sql
--liquibase formatted sql

--changeset mak.dz:always-random-units runAlways:true
--preconditions onFail:MARK_RAN
INSERT INTO units (rooms_number, accommodation_type, floor, base_cost, description, is_available)
SELECT floor(random() * 5) + 1                                             AS rooms_number,
       (ARRAY ['HOME'::accommodation_type,
           'FLAT'::accommodation_type,
           'APARTMENTS'::accommodation_type])[floor(random() * 3) + 1]     AS accommodation_type,
       floor(random() * 20) + 1                                            AS floor,
       (floor(random() * 900) + 100)::decimal                              AS base_cost,
       'Modern apartment with ' ||
       (ARRAY ['park',
           'sea',
           'city',
           'mountain',
           'garden'])[floor(random() * 5) + 1] ||
       ' view. ' ||
       (ARRAY ['Fully furnished',
           'Recently renovated',
           'Modern design',
           'Spacious',
           'Luxury'])[floor(random() * 5) + 1] ||
       ' with household appliances.'                                       AS description,
       TRUE                                                                AS is_available
FROM generate_series(1, 90);
