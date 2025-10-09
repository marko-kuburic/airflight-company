-- Complete Meaningful Test Data for Air Company (Updated with Historical Services)
-- Based on current time: 7pm Serbia, October 9th, 2025
-- Flight statuses are time-aware:
-- - COMPLETED (LANDED): flights that ended before current time (7pm)
-- - CANCELLED: can be at any time
-- - DELAYED: flights scheduled for today but delayed
-- - BOARDING/DEPARTED: flights happening around current time
-- - SCHEDULED: future flights

-- =============================================================================
-- STEP 1: CLEAR EXISTING DATA
-- =============================================================================

-- Delete in correct order to avoid foreign key constraints
DELETE FROM offers;
DELETE FROM tickets;
DELETE FROM flights;
DELETE FROM segments;
DELETE FROM routes;
DELETE FROM services;
DELETE FROM aircraft;

-- Reset auto increment counters
ALTER TABLE offers AUTO_INCREMENT = 1;
ALTER TABLE tickets AUTO_INCREMENT = 1;
ALTER TABLE flights AUTO_INCREMENT = 1;
ALTER TABLE routes AUTO_INCREMENT = 1;
ALTER TABLE segments AUTO_INCREMENT = 1;
ALTER TABLE aircraft AUTO_INCREMENT = 1;
ALTER TABLE services AUTO_INCREMENT = 1;

-- =============================================================================
-- STEP 2: INSERT AIRCRAFT
-- =============================================================================

INSERT INTO aircraft (registration, model, status, capacity, created_at, modified_at) VALUES
('YU-ABC', 'Boeing 737-800', 'ACTIVE', 189, NOW(), NOW()),
('YU-DEF', 'Airbus A320', 'ACTIVE', 180, NOW(), NOW()),
('YU-GHI', 'Boeing 777-300ER', 'ACTIVE', 396, NOW(), NOW()),
('YU-JKL', 'Airbus A321', 'ACTIVE', 220, NOW(), NOW()),
('YU-MNO', 'Boeing 737-800', 'MAINTENANCE', 189, NOW(), NOW());

-- =============================================================================
-- STEP 3: INSERT ROUTES WITH SEGMENTS
-- =============================================================================

-- Route 1: Belgrade to Munich (completed flights)
INSERT INTO routes (name, total_distance, created_at, modified_at) VALUES
('BEG-MUC', 1423.11, NOW(), NOW());

SET @route1_id = LAST_INSERT_ID();

INSERT INTO segments (route_id, origin_airport_id, destination_airport_id, distance, duration_minutes, layover_minutes, created_at, modified_at) VALUES
(@route1_id, (SELECT id FROM airports WHERE iata_code = 'BEG'), (SELECT id FROM airports WHERE iata_code = 'MUC'), 1423.11, 125, 0, NOW(), NOW());

-- Route 2: Munich to London (delayed/cancelled flights)
INSERT INTO routes (name, total_distance, created_at, modified_at) VALUES
('MUC-LHR', 950.45, NOW(), NOW());

SET @route2_id = LAST_INSERT_ID();

INSERT INTO segments (route_id, origin_airport_id, destination_airport_id, distance, duration_minutes, layover_minutes, created_at, modified_at) VALUES
(@route2_id, (SELECT id FROM airports WHERE iata_code = 'MUC'), (SELECT id FROM airports WHERE iata_code = 'LHR'), 950.45, 105, 0, NOW(), NOW());

-- Route 3: Belgrade to Frankfurt (boarding/departed flights)
INSERT INTO routes (name, total_distance, created_at, modified_at) VALUES
('BEG-FRA', 1200.33, NOW(), NOW());

SET @route3_id = LAST_INSERT_ID();

INSERT INTO segments (route_id, origin_airport_id, destination_airport_id, distance, duration_minutes, layover_minutes, created_at, modified_at) VALUES
(@route3_id, (SELECT id FROM airports WHERE iata_code = 'BEG'), (SELECT id FROM airports WHERE iata_code = 'FRA'), 1200.33, 115, 0, NOW(), NOW());

-- Route 4: Frankfurt to New York (scheduled flights)
INSERT INTO routes (name, total_distance, created_at, modified_at) VALUES
('FRA-JFK', 6200.89, NOW(), NOW());

SET @route4_id = LAST_INSERT_ID();

INSERT INTO segments (route_id, origin_airport_id, destination_airport_id, distance, duration_minutes, layover_minutes, created_at, modified_at) VALUES
(@route4_id, (SELECT id FROM airports WHERE iata_code = 'FRA'), (SELECT id FROM airports WHERE iata_code = 'JFK'), 6200.89, 480, 0, NOW(), NOW());

-- Route 5: Multi-segment route (Belgrade -> Munich -> London)
INSERT INTO routes (name, total_distance, created_at, modified_at) VALUES
('BEG-MUC-LHR', 2373.56, NOW(), NOW());

SET @route5_id = LAST_INSERT_ID();

INSERT INTO segments (route_id, origin_airport_id, destination_airport_id, distance, duration_minutes, layover_minutes, created_at, modified_at) VALUES
(@route5_id, (SELECT id FROM airports WHERE iata_code = 'BEG'), (SELECT id FROM airports WHERE iata_code = 'MUC'), 1423.11, 125, 45, NOW(), NOW()),
(@route5_id, (SELECT id FROM airports WHERE iata_code = 'MUC'), (SELECT id FROM airports WHERE iata_code = 'LHR'), 950.45, 105, 0, NOW(), NOW());

-- =============================================================================
-- STEP 4: INSERT MAINTENANCE/SERVICES (Including Historical Data)
-- =============================================================================

-- Current services
INSERT INTO services (aircraft_id, technician_id, service_type, description, start_date, end_date, status, created_at, modified_at) VALUES
((SELECT id FROM aircraft WHERE registration = 'YU-MNO'), (SELECT id FROM users WHERE email = 'milan.tech@aircompany.com'), 'SCHEDULED', 'Regular maintenance check', '2025-10-08 14:00:00', '2025-10-10 18:00:00', 'IN_PROGRESS', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-ABC'), (SELECT id FROM users WHERE email = 'milan.tech@aircompany.com'), 'INSPECTION', 'Pre-flight inspection', '2025-10-09 16:00:00', '2025-10-09 16:30:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-DEF'), (SELECT id FROM users WHERE email = 'jelena.tech@aircompany.com'), 'UNSCHEDULED', 'Engine repair', '2025-10-07 08:00:00', '2025-10-09 12:00:00', 'COMPLETED', NOW(), NOW()),

-- Historical services for YU-ABC (Boeing 737-800)
((SELECT id FROM aircraft WHERE registration = 'YU-ABC'), (SELECT id FROM users WHERE email = 'milan.tech@aircompany.com'), 'SCHEDULED', 'Monthly inspection', '2025-09-15 08:00:00', '2025-09-15 12:00:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-ABC'), (SELECT id FROM users WHERE email = 'jelena.tech@aircompany.com'), 'UNSCHEDULED', 'Landing gear repair', '2025-09-20 14:00:00', '2025-09-21 10:00:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-ABC'), (SELECT id FROM users WHERE email = 'nikola.tech@aircompany.com'), 'INSPECTION', 'Pre-flight safety check', '2025-10-01 06:00:00', '2025-10-01 07:30:00', 'COMPLETED', NOW(), NOW()),

-- Historical services for YU-DEF (Airbus A320)
((SELECT id FROM aircraft WHERE registration = 'YU-DEF'), (SELECT id FROM users WHERE email = 'milan.tech@aircompany.com'), 'SCHEDULED', 'Quarterly maintenance', '2025-09-10 09:00:00', '2025-09-12 16:00:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-DEF'), (SELECT id FROM users WHERE email = 'jelena.tech@aircompany.com'), 'INSPECTION', 'Engine inspection', '2025-09-25 10:00:00', '2025-09-25 14:00:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-DEF'), (SELECT id FROM users WHERE email = 'svetlana.tech@aircompany.com'), 'UNSCHEDULED', 'Hydraulic system repair', '2025-10-05 08:00:00', '2025-10-06 18:00:00', 'COMPLETED', NOW(), NOW()),

-- Historical services for YU-GHI (Boeing 777-300ER)
((SELECT id FROM aircraft WHERE registration = 'YU-GHI'), (SELECT id FROM users WHERE email = 'jelena.tech@aircompany.com'), 'SCHEDULED', 'Annual maintenance', '2025-08-20 08:00:00', '2025-08-25 18:00:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-GHI'), (SELECT id FROM users WHERE email = 'nikola.tech@aircompany.com'), 'INSPECTION', 'Avionics check', '2025-09-05 09:00:00', '2025-09-05 15:00:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-GHI'), (SELECT id FROM users WHERE email = 'milan.tech@aircompany.com'), 'SCHEDULED', 'Monthly inspection', '2025-09-28 07:00:00', '2025-09-28 11:00:00', 'COMPLETED', NOW(), NOW()),

-- Historical services for YU-JKL (Airbus A321)
((SELECT id FROM aircraft WHERE registration = 'YU-JKL'), (SELECT id FROM users WHERE email = 'nikola.tech@aircompany.com'), 'SCHEDULED', 'Quarterly maintenance', '2025-09-01 08:00:00', '2025-09-03 17:00:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-JKL'), (SELECT id FROM users WHERE email = 'svetlana.tech@aircompany.com'), 'INSPECTION', 'Cabin safety check', '2025-09-18 10:00:00', '2025-09-18 13:00:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-JKL'), (SELECT id FROM users WHERE email = 'milan.tech@aircompany.com'), 'UNSCHEDULED', 'Navigation system repair', '2025-10-02 14:00:00', '2025-10-03 09:00:00', 'COMPLETED', NOW(), NOW()),

-- Additional current services
((SELECT id FROM aircraft WHERE registration = 'YU-DEF'), (SELECT id FROM users WHERE email = 'jelena.tech@aircompany.com'), 'SCHEDULED', 'Pre-flight inspection', '2025-10-09 15:00:00', '2025-10-09 15:45:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-GHI'), (SELECT id FROM users WHERE email = 'nikola.tech@aircompany.com'), 'INSPECTION', 'Post-flight check', '2025-10-09 17:00:00', '2025-10-09 17:30:00', 'COMPLETED', NOW(), NOW()),
((SELECT id FROM aircraft WHERE registration = 'YU-JKL'), (SELECT id FROM users WHERE email = 'svetlana.tech@aircompany.com'), 'SCHEDULED', 'Evening inspection', '2025-10-09 18:00:00', '2025-10-09 19:00:00', 'COMPLETED', NOW(), NOW());

-- =============================================================================
-- STEP 5: INSERT FLIGHTS WITH MEANINGFUL TIMESTAMPS
-- Current time reference: 2025-10-09 19:00:00 (7pm Serbia time)
-- =============================================================================

-- COMPLETED flights (ended before current time)
INSERT INTO flights (flight_number, route_id, aircraft_id, flight_dispatcher_id, dep_time, arr_time, status, created_at, modified_at) VALUES
('FD-001', @route1_id, (SELECT id FROM aircraft WHERE registration = 'YU-ABC'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 08:00:00', '2025-10-09 10:05:00', 'LANDED', NOW(), NOW()),
('FD-002', @route1_id, (SELECT id FROM aircraft WHERE registration = 'YU-DEF'), (SELECT id FROM users WHERE email = 'ana.dispatcher@aircompany.com'), '2025-10-09 12:00:00', '2025-10-09 14:05:00', 'LANDED', NOW(), NOW()),
('FD-003', @route2_id, (SELECT id FROM aircraft WHERE registration = 'YU-GHI'), (SELECT id FROM users WHERE email = 'stefan.dispatcher@aircompany.com'), '2025-10-09 10:00:00', '2025-10-09 11:45:00', 'LANDED', NOW(), NOW()),
('FD-004', @route3_id, (SELECT id FROM aircraft WHERE registration = 'YU-JKL'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 06:00:00', '2025-10-09 07:55:00', 'LANDED', NOW(), NOW()),
('FD-005', @route5_id, (SELECT id FROM aircraft WHERE registration = 'YU-ABC'), (SELECT id FROM users WHERE email = 'ana.dispatcher@aircompany.com'), '2025-10-09 09:00:00', '2025-10-09 12:15:00', 'LANDED', NOW(), NOW());

-- CANCELLED flights (can be at any time)
INSERT INTO flights (flight_number, route_id, aircraft_id, flight_dispatcher_id, dep_time, arr_time, status, created_at, modified_at) VALUES
('FD-101', @route2_id, (SELECT id FROM aircraft WHERE registration = 'YU-DEF'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 15:00:00', '2025-10-09 16:45:00', 'CANCELLED', NOW(), NOW()),
('FD-102', @route4_id, (SELECT id FROM aircraft WHERE registration = 'YU-GHI'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-08 22:00:00', '2025-10-09 06:00:00', 'CANCELLED', NOW(), NOW()),
('FD-103', @route1_id, (SELECT id FROM aircraft WHERE registration = 'YU-JKL'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 18:00:00', '2025-10-09 20:05:00', 'CANCELLED', NOW(), NOW());

-- DELAYED flights (scheduled for today but delayed)
INSERT INTO flights (flight_number, route_id, aircraft_id, flight_dispatcher_id, dep_time, arr_time, status, created_at, modified_at) VALUES
('FD-201', @route3_id, (SELECT id FROM aircraft WHERE registration = 'YU-ABC'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 16:00:00', '2025-10-09 17:55:00', 'DELAYED', NOW(), NOW()),
('FD-202', @route2_id, (SELECT id FROM aircraft WHERE registration = 'YU-DEF'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 17:30:00', '2025-10-09 19:15:00', 'DELAYED', NOW(), NOW()),
('FD-203', @route1_id, (SELECT id FROM aircraft WHERE registration = 'YU-GHI'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 18:30:00', '2025-10-09 20:35:00', 'DELAYED', NOW(), NOW()),
('FD-204', @route5_id, (SELECT id FROM aircraft WHERE registration = 'YU-JKL'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 19:00:00', '2025-10-09 22:15:00', 'DELAYED', NOW(), NOW());

-- BOARDING flights (happening around current time)
INSERT INTO flights (flight_number, route_id, aircraft_id, flight_dispatcher_id, dep_time, arr_time, status, created_at, modified_at) VALUES
('FD-301', @route3_id, (SELECT id FROM aircraft WHERE registration = 'YU-ABC'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 19:30:00', '2025-10-09 21:25:00', 'BOARDING', NOW(), NOW()),
('FD-302', @route2_id, (SELECT id FROM aircraft WHERE registration = 'YU-DEF'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 19:45:00', '2025-10-09 21:30:00', 'BOARDING', NOW(), NOW());

-- DEPARTED flights (just left)
INSERT INTO flights (flight_number, route_id, aircraft_id, flight_dispatcher_id, dep_time, arr_time, status, created_at, modified_at) VALUES
('FD-401', @route4_id, (SELECT id FROM aircraft WHERE registration = 'YU-GHI'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 18:45:00', '2025-10-10 02:45:00', 'DEPARTED', NOW(), NOW()),
('FD-402', @route1_id, (SELECT id FROM aircraft WHERE registration = 'YU-JKL'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-09 18:30:00', '2025-10-09 20:35:00', 'DEPARTED', NOW(), NOW());

-- SCHEDULED flights (future flights)
INSERT INTO flights (flight_number, route_id, aircraft_id, flight_dispatcher_id, dep_time, arr_time, status, created_at, modified_at) VALUES
('FD-501', @route4_id, (SELECT id FROM aircraft WHERE registration = 'YU-GHI'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-10 10:00:00', '2025-10-10 18:00:00', 'SCHEDULED', NOW(), NOW()),
('FD-502', @route1_id, (SELECT id FROM aircraft WHERE registration = 'YU-ABC'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-10 14:00:00', '2025-10-10 16:05:00', 'SCHEDULED', NOW(), NOW()),
('FD-503', @route3_id, (SELECT id FROM aircraft WHERE registration = 'YU-DEF'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-10 16:00:00', '2025-10-10 17:55:00', 'SCHEDULED', NOW(), NOW()),
('FD-504', @route2_id, (SELECT id FROM aircraft WHERE registration = 'YU-JKL'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-10 18:00:00', '2025-10-10 19:45:00', 'SCHEDULED', NOW(), NOW()),
('FD-505', @route5_id, (SELECT id FROM aircraft WHERE registration = 'YU-ABC'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-11 09:00:00', '2025-10-11 12:15:00', 'SCHEDULED', NOW(), NOW()),
('FD-506', @route4_id, (SELECT id FROM aircraft WHERE registration = 'YU-GHI'), (SELECT id FROM users WHERE email = 'dispatcher@aircompany.com'), '2025-10-11 22:00:00', '2025-10-12 06:00:00', 'SCHEDULED', NOW(), NOW());

-- =============================================================================
-- STEP 6: VERIFICATION AND SUMMARY
-- =============================================================================

SELECT '=== MEANINGFUL TEST DATA WITH HISTORICAL SERVICES INSERTED SUCCESSFULLY ===' as status;

SELECT 
    'Flight Status Summary' as category,
    status,
    COUNT(*) as count,
    CASE status
        WHEN 'LANDED' THEN 'Flights that completed before 7pm today'
        WHEN 'CANCELLED' THEN 'Flights cancelled at various times'
        WHEN 'DELAYED' THEN 'Flights scheduled for today but delayed'
        WHEN 'BOARDING' THEN 'Flights boarding around current time (7pm)'
        WHEN 'DEPARTED' THEN 'Flights that just departed'
        WHEN 'SCHEDULED' THEN 'Future flights scheduled'
        ELSE 'Other status'
    END as description
FROM flights 
GROUP BY status 
ORDER BY 
    CASE status 
        WHEN 'LANDED' THEN 1
        WHEN 'CANCELLED' THEN 2
        WHEN 'DELAYED' THEN 3
        WHEN 'BOARDING' THEN 4
        WHEN 'DEPARTED' THEN 5
        WHEN 'SCHEDULED' THEN 6
        ELSE 7
    END;

SELECT 
    'Aircraft Summary' as category,
    registration,
    model,
    status,
    CASE status
        WHEN 'ACTIVE' THEN 'Available for flights'
        WHEN 'MAINTENANCE' THEN 'Under maintenance'
        ELSE 'Other status'
    END as description
FROM aircraft 
ORDER BY registration;

SELECT 
    'Routes Summary' as category,
    name,
    total_distance,
    (SELECT COUNT(*) FROM segments WHERE route_id = routes.id) as segment_count
FROM routes 
ORDER BY name;

SELECT 
    'Services Summary' as category,
    service_type,
    status,
    COUNT(*) as count,
    CASE status
        WHEN 'IN_PROGRESS' THEN 'Currently being performed'
        WHEN 'COMPLETED' THEN 'Successfully completed'
        ELSE 'Other status'
    END as description
FROM services 
GROUP BY service_type, status 
ORDER BY service_type, status;
