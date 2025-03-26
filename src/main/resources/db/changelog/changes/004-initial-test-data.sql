--liquibase formatted sql

--changeset mak.dz:1  dbms:postgresql context:test-data
DELETE FROM users;
INSERT INTO users (username, email)
VALUES
    ('test_user', 'test.user@example.com');

DELETE FROM units;
INSERT INTO units (rooms_number, accommodation_type, floor, base_cost, description)
VALUES
    (2, 'FLAT', 3, 100.00, 'Современная квартира с видом на парк. Полностью меблирована, есть бытовая техника'),
    (3, 'APARTMENTS', 5, 150.00, 'Просторные апартаменты с панорамными окнами. Отдельная кухня-столовая'),
    (1, 'FLAT', 2, 80.00, 'Уютная студия в историческом центре. Идеально для одного или пары'),
    (4, 'HOME', 1, 200.00, 'Частный дом с садом. Гараж на две машины, зона барбекю'),
    (2, 'APARTMENTS', 4, 120.00, 'Светлые апартаменты с современным ремонтом. Два санузла'),
    (3, 'HOME', 2, 180.00, 'Двухэтажный таунхаус с террасой. Закрытая территория'),
    (1, 'FLAT', 7, 90.00, 'Компактная квартира с высокими потолками. Встроенная кухня'),
    (2, 'FLAT', 3, 110.00, 'Квартира с раздельными комнатами. Свежий ремонт, кондиционер'),
    (5, 'APARTMENTS', 6, 250.00, 'Пентхаус с террасой. Панорамный вид на город'),
    (2, 'HOME', 1, 160.00, 'Небольшой дом с участком. Автономное отопление');

DELETE FROM events;
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
