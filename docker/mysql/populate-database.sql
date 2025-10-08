-- Database initialization script for Air Company
-- This script populates the database with entities related to the flight dispatcher subsystem

-- Clear existing data (in correct order to avoid foreign key constraints)
DELETE FROM services;
DELETE FROM aircraft;
DELETE FROM airports;
DELETE FROM countries;
DELETE FROM users;

-- Reset auto-increment counters
ALTER TABLE services AUTO_INCREMENT = 1;
ALTER TABLE aircraft AUTO_INCREMENT = 1;
ALTER TABLE airports AUTO_INCREMENT = 1;
ALTER TABLE countries AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;

-- Insert Countries
INSERT INTO countries (code, name, created_at, modified_at) VALUES
('RS', 'Serbia', NOW(), NOW()),
('FR', 'France', NOW(), NOW()),
('DE', 'Germany', NOW(), NOW()),
('IT', 'Italy', NOW(), NOW()),
('ES', 'Spain', NOW(), NOW()),
('GB', 'United Kingdom', NOW(), NOW()),
('US', 'United States', NOW(), NOW()),
('CA', 'Canada', NOW(), NOW()),
('AU', 'Australia', NOW(), NOW()),
('JP', 'Japan', NOW(), NOW());

-- Insert Airports
INSERT INTO airports (iata_code, name, city, country_code, latitude, longitude, timezone, created_at, modified_at) VALUES
('BEG', 'Belgrade Nikola Tesla Airport', 'Belgrade', 'RS', 44.8194, 20.3069, 'Europe/Belgrade', NOW(), NOW()),
('CDG', 'Charles de Gaulle Airport', 'Paris', 'FR', 49.0097, 2.5479, 'Europe/Paris', NOW(), NOW()),
('ORY', 'Orly Airport', 'Paris', 'FR', 48.7233, 2.3794, 'Europe/Paris', NOW(), NOW()),
('FRA', 'Frankfurt Airport', 'Frankfurt', 'DE', 50.0379, 8.5622, 'Europe/Berlin', NOW(), NOW()),
('MUC', 'Munich Airport', 'Munich', 'DE', 48.3538, 11.7861, 'Europe/Berlin', NOW(), NOW()),
('FCO', 'Leonardo da Vinci Airport', 'Rome', 'IT', 41.8003, 12.2389, 'Europe/Rome', NOW(), NOW()),
('MAD', 'Adolfo Suárez Madrid-Barajas Airport', 'Madrid', 'ES', 40.4839, -3.5680, 'Europe/Madrid', NOW(), NOW()),
('LHR', 'Heathrow Airport', 'London', 'GB', 51.4700, -0.4543, 'Europe/London', NOW(), NOW()),
('JFK', 'John F. Kennedy International Airport', 'New York', 'US', 40.6413, -73.7781, 'America/New_York', NOW(), NOW()),
('LAX', 'Los Angeles International Airport', 'Los Angeles', 'US', 33.9425, -118.4081, 'America/Los_Angeles', NOW(), NOW());

-- Insert Flight Dispatcher Users
INSERT INTO users (user_type, first_name, last_name, email, password, dispatch_license, experience_years, aircraft_types_qualified, created_at, modified_at) VALUES
('FLIGHT_DISPATCHER', 'Marko', 'Petrovic', 'dispatcher@aircompany.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'FD-001-2024', 5, 'A320,A321,B737,B777', NOW(), NOW()),
('FLIGHT_DISPATCHER', 'Ana', 'Nikolic', 'ana.dispatcher@aircompany.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'FD-002-2024', 3, 'A320,B737', NOW(), NOW()),
('FLIGHT_DISPATCHER', 'Stefan', 'Jovanovic', 'stefan.dispatcher@aircompany.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'FD-003-2024', 7, 'A320,A321,A330,B737,B777,B787', NOW(), NOW());

-- Insert Technician Users
INSERT INTO users (user_type, first_name, last_name, email, password, experience_years, specialization, created_at, modified_at) VALUES
('TECHNICIAN', 'Milan', 'Djordjevic', 'milan.tech@aircompany.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 8, 'Aircraft Engine Maintenance', NOW(), NOW()),
('TECHNICIAN', 'Jelena', 'Stojanovic', 'jelena.tech@aircompany.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 6, 'Avionics Systems', NOW(), NOW()),
('TECHNICIAN', 'Nikola', 'Milosevic', 'nikola.tech@aircompany.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 10, 'Structural Repair', NOW(), NOW()),
('TECHNICIAN', 'Svetlana', 'Radovic', 'svetlana.tech@aircompany.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 4, 'Hydraulic Systems', NOW(), NOW());

-- Insert Aircraft
INSERT INTO aircraft (registration, model, status, capacity, created_at, modified_at) VALUES
('YU-ABC', 'Airbus A320', 'ACTIVE', 180, NOW(), NOW()),
('YU-DEF', 'Airbus A320', 'ACTIVE', 180, NOW(), NOW()),
('YU-GHI', 'Airbus A321', 'ACTIVE', 220, NOW(), NOW()),
('YU-JKL', 'Boeing 737', 'ACTIVE', 189, NOW(), NOW()),
('YU-MNO', 'Boeing 737', 'MAINTENANCE', 189, NOW(), NOW()),
('YU-PQR', 'Airbus A330', 'ACTIVE', 300, NOW(), NOW()),
('YU-STU', 'Boeing 777', 'OUT_OF_SERVICE', 350, NOW(), NOW()),
('YU-VWX', 'Embraer E190', 'ACTIVE', 100, NOW(), NOW()),
('YU-YZA', 'ATR 72', 'ACTIVE', 72, NOW(), NOW()),
('YU-BCD', 'Airbus A320', 'OUT_OF_SERVICE', 180, NOW(), NOW());

-- Insert Service Records
INSERT INTO services (aircraft_id, technician_id, service_type, description, start_date, end_date, status, created_at, modified_at) VALUES
(5, 1, 'SCHEDULED', 'Regular engine inspection and oil change', '2024-10-05', '2024-10-05', 'COMPLETED', NOW(), NOW()),
(2, 2, 'UNSCHEDULED', 'Avionics system calibration and testing', '2024-10-06', NULL, 'IN_PROGRESS', NOW(), NOW()),
(7, 3, 'SCHEDULED', 'Structural inspection and repair', '2024-10-10', NULL, 'SCHEDULED', NOW(), NOW()),
(1, 4, 'SCHEDULED', 'Hydraulic system maintenance', '2024-10-07', NULL, 'SCHEDULED', NOW(), NOW()),
(3, 1, 'UNSCHEDULED', 'Engine vibration analysis', '2024-10-08', NULL, 'SCHEDULED', NOW(), NOW()),
(4, 2, 'INSPECTION', 'Cabin pressurization system check', '2024-10-09', NULL, 'SCHEDULED', NOW(), NOW());

-- Display summary of inserted data
SELECT 'Countries' as Table_Name, COUNT(*) as Records FROM countries
UNION ALL
SELECT 'Airports', COUNT(*) FROM airports
UNION ALL
SELECT 'Flight Dispatchers', COUNT(*) FROM users WHERE user_type = 'FLIGHT_DISPATCHER'
UNION ALL
SELECT 'Technicians', COUNT(*) FROM users WHERE user_type = 'TECHNICIAN'
UNION ALL
SELECT 'Aircraft', COUNT(*) FROM aircraft
UNION ALL
SELECT 'Service Records', COUNT(*) FROM services;