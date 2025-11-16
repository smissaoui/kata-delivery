WITH days AS (
    SELECT CURRENT_DATE + d AS date_slot
    FROM generate_series(0, 4) AS d
),
     slots AS (
         SELECT
             date_slot,
             make_time(8, 0, 0) + (i * interval '30 minutes') AS start_time,
             make_time(8, 0, 0) + ((i + 1) * interval '30 minutes') AS end_time
FROM days
    CROSS JOIN generate_series(0, ((23 - 8) * 2) - 1) AS i
    ),
    modes AS (
SELECT * FROM (VALUES
    ('DRIVE', 5),
    ('DELIVERY', 3),
    ('DELIVERY_TODAY', 2),
    ('DELIVERY_ASAP', 1)
    ) AS m(delivery_mode, capacity)
    )
INSERT INTO time_slot (delivery_mode, date, start_time, end_time, capacity)
SELECT
    m.delivery_mode,
    s.date_slot,
    s.start_time,
    s.end_time,
    m.capacity
FROM slots s
         CROSS JOIN modes m
ORDER BY s.date_slot, s.start_time, m.delivery_mode;
