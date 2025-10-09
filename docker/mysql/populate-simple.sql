-- Simple populate script that works with current models
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
('TECHNICIAN', 'Ana', 'Nikolic', 'technician@aircompany.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 3, 'Engine Maintenance,Avionics', NOW(), NOW()),
('TECHNICIAN', 'Milan', 'Popovic', 'milan.technician@aircompany.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 5, 'Engine Maintenance,Landing Gear', NOW(), NOW()),
('TECHNICIAN', 'Jelena', 'Markovic', 'jelena.technician@aircompany.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 4, 'Avionics,Electrical Systems', NOW(), NOW());

-- Insert Customer Users
INSERT INTO users (user_type, first_name, last_name, email, password, created_at, modified_at) VALUES
('CUSTOMER', 'John', 'Doe', 'john@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NOW(), NOW()),
('CUSTOMER', 'Jane', 'Smith', 'jane@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NOW(), NOW());

-- Insert Routes
INSERT INTO routes (name, total_distance, created_at, modified_at) VALUES
('BEG-FRA', 1000.0, NOW(), NOW()),
('FRA-CDG', 500.0, NOW(), NOW()),
('CDG-LHR', 300.0, NOW(), NOW()),
('LHR-JFK', 5500.0, NOW(), NOW()),
('JFK-LAX', 2500.0, NOW(), NOW()),
('LAX-MAD', 6000.0, NOW(), NOW());

-- Insert Segments with time fields
INSERT INTO segments (route_id, origin_airport_id, destination_airport_id, distance, departure_time, arrival_time, created_at, modified_at) VALUES
(1, 1, 4, 1000.0, '12:00:00', '13:25:00', NOW(), NOW()),
(2, 4, 2, 500.0, '14:30:00', '15:45:00', NOW(), NOW()),
(3, 2, 8, 300.0, '16:00:00', '17:15:00', NOW(), NOW()),
(4, 8, 9, 5500.0, '18:00:00', '23:30:00', NOW(), NOW()),
(5, 9, 10, 2500.0, '01:00:00', '04:30:00', NOW(), NOW()),
(6, 10, 7, 6000.0, '06:00:00', '14:30:00', NOW(), NOW());

