-- Presentation Data for Air Company System
-- Updated to match actual database schema

USE air_company;

-- Get existing customer IDs
SET @customer1_id = 1; -- Danilo
SET @customer2_id = 2; -- Iva  
SET @customer3_id = 3; -- Marko

-- Get some flight IDs for creating reservations
SET @flight1_id = (SELECT flight_id FROM flights ORDER BY RAND() LIMIT 1);
SET @flight2_id = (SELECT flight_id FROM flights WHERE flight_id != @flight1_id ORDER BY RAND() LIMIT 1);
SET @flight3_id = (SELECT flight_id FROM flights WHERE flight_id NOT IN (@flight1_id, @flight2_id) ORDER BY RAND() LIMIT 1);
SET @flight4_id = (SELECT flight_id FROM flights WHERE flight_id NOT IN (@flight1_id, @flight2_id, @flight3_id) ORDER BY RAND() LIMIT 1);

-- Add Loyalty Programs for existing customers if they don't have one
INSERT IGNORE INTO loyalty (customer_id, points, tier, joined_date, last_updated)
VALUES 
(@customer1_id, 15000, 'SILVER', '2024-01-10', NOW()),
(@customer2_id, 45000, 'GOLD', '2024-02-15', NOW()),
(@customer3_id, 8500, 'BRONZE', '2024-03-20', NOW())
ON DUPLICATE KEY UPDATE 
    points = VALUES(points),
    tier = VALUES(tier);

-- Add saved Payment Methods for customers
INSERT INTO saved_payment_methods (user_id, card_number, card_holder_name, expiry_date, cvv, is_default, created_at)
VALUES 
(@customer1_id, '4532123456789012', 'DANILO DAMJANOVIC', '2026-12-31', '123', TRUE, NOW()),
(@customer1_id, '5425123456789012', 'DANILO DAMJANOVIC', '2027-06-30', '456', FALSE, NOW()),
(@customer2_id, '4916123456789012', 'IVA JOVANOVIC', '2026-08-31', '789', TRUE, NOW()),
(@customer3_id, '4539123456789012', 'MARKO KUBURIC', '2027-03-31', '234', TRUE, NOW());

-- ========================================
-- PAST COMPLETED FLIGHTS (USED TICKETS)
-- ========================================

-- Past Flight 1 - Danilo (Economy, used from August)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers, created_at)
VALUES (@customer1_id, @flight1_id, '2024-08-05 10:30:00', 'CONFIRMED', 450.00, 1, '2024-08-05 10:30:00');

SET @res_past1 = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date, departure_time, arrival_time)
SELECT @res_past1, @customer1_id, @flight1_id, '12A', 'ECONOMY', 450.00, 'USED', '2024-08-05 10:30:00', 
       DATE_SUB(NOW(), INTERVAL 60 DAY), 
       DATE_SUB(NOW(), INTERVAL 60 DAY) + INTERVAL 3 HOUR;

SET @ticket_past1 = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status, transaction_id, created_at)
VALUES (@ticket_past1, 450.00, 'CREDIT_CARD', '2024-08-05 10:35:00', 'COMPLETED', CONCAT('TXN', FLOOR(RAND() * 1000000)), '2024-08-05 10:35:00');

-- Past Flight 2 - Iva (Business Class, used from July)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers, created_at)
VALUES (@customer2_id, @flight2_id, '2024-07-15 14:20:00', 'CONFIRMED', 1200.00, 1, '2024-07-15 14:20:00');

SET @res_past2 = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date, departure_time, arrival_time)
SELECT @res_past2, @customer2_id, @flight2_id, '3B', 'BUSINESS', 1200.00, 'USED', '2024-07-15 14:20:00',
       DATE_SUB(NOW(), INTERVAL 85 DAY),
       DATE_SUB(NOW(), INTERVAL 85 DAY) + INTERVAL 4 HOUR;

SET @ticket_past2 = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status, transaction_id, created_at)
VALUES (@ticket_past2, 1200.00, 'CREDIT_CARD', '2024-07-15 14:25:00', 'COMPLETED', CONCAT('TXN', FLOOR(RAND() * 1000000)), '2024-07-15 14:25:00');

-- Past Flight 3 - Marko with family (3 passengers, used from September)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers, created_at)
VALUES (@customer3_id, @flight3_id, '2024-09-01 09:00:00', 'CONFIRMED', 1350.00, 3, '2024-09-01 09:00:00');

SET @res_past3 = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date, departure_time, arrival_time)
SELECT @res_past3, @customer3_id, @flight3_id, '15A', 'ECONOMY', 450.00, 'USED', '2024-09-01 09:00:00',
       DATE_SUB(NOW(), INTERVAL 37 DAY),
       DATE_SUB(NOW(), INTERVAL 37 DAY) + INTERVAL 2 HOUR;

SET @ticket_past3a = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date, departure_time, arrival_time)
SELECT @res_past3, @customer3_id, @flight3_id, '15B', 'ECONOMY', 450.00, 'USED', '2024-09-01 09:00:00',
       DATE_SUB(NOW(), INTERVAL 37 DAY),
       DATE_SUB(NOW(), INTERVAL 37 DAY) + INTERVAL 2 HOUR;

SET @ticket_past3b = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date, departure_time, arrival_time)
SELECT @res_past3, @customer3_id, @flight3_id, '15C', 'ECONOMY', 450.00, 'USED', '2024-09-01 09:00:00',
       DATE_SUB(NOW(), INTERVAL 37 DAY),
       DATE_SUB(NOW(), INTERVAL 37 DAY) + INTERVAL 2 HOUR;

SET @ticket_past3c = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status, transaction_id, created_at)
VALUES 
(@ticket_past3a, 450.00, 'CREDIT_CARD', '2024-09-01 09:05:00', 'COMPLETED', CONCAT('TXN', FLOOR(RAND() * 1000000)), '2024-09-01 09:05:00'),
(@ticket_past3b, 450.00, 'CREDIT_CARD', '2024-09-01 09:05:00', 'COMPLETED', CONCAT('TXN', FLOOR(RAND() * 1000000)), '2024-09-01 09:05:00'),
(@ticket_past3c, 450.00, 'CREDIT_CARD', '2024-09-01 09:05:00', 'COMPLETED', CONCAT('TXN', FLOOR(RAND() * 1000000)), '2024-09-01 09:05:00');

-- ========================================
-- CANCELLED RESERVATIONS
-- ========================================

-- Cancelled 1 - Danilo (cancelled last month)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers, created_at)
VALUES (@customer1_id, @flight2_id, '2024-09-20 11:00:00', 'CANCELLED', 550.00, 1, '2024-09-20 11:00:00');

SET @res_cancel1 = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date, departure_time, arrival_time)
SELECT @res_cancel1, @customer1_id, @flight2_id, '20D', 'ECONOMY', 550.00, 'CANCELLED', '2024-09-20 11:00:00',
       DATE_ADD(NOW(), INTERVAL 5 DAY),
       DATE_ADD(NOW(), INTERVAL 5 DAY) + INTERVAL 3 HOUR;

SET @ticket_cancel1 = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status, transaction_id, created_at)
VALUES (@ticket_cancel1, 550.00, 'CREDIT_CARD', '2024-09-20 11:05:00', 'REFUNDED', CONCAT('TXN', FLOOR(RAND() * 1000000)), '2024-09-20 11:05:00');

-- Cancelled 2 - Iva (refunded ticket)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers, created_at)
VALUES (@customer2_id, @flight3_id, '2024-08-25 16:30:00', 'CANCELLED', 480.00, 1, '2024-08-25 16:30:00');

SET @res_cancel2 = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date, departure_time, arrival_time)
SELECT @res_cancel2, @customer2_id, @flight3_id, '18F', 'ECONOMY', 480.00, 'REFUNDED', '2024-08-25 16:30:00',
       DATE_ADD(NOW(), INTERVAL 10 DAY),
       DATE_ADD(NOW(), INTERVAL 10 DAY) + INTERVAL 2 HOUR;

SET @ticket_cancel2 = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status, transaction_id, created_at)
VALUES (@ticket_cancel2, 480.00, 'CREDIT_CARD', '2024-08-25 16:35:00', 'REFUNDED', CONCAT('TXN', FLOOR(RAND() * 1000000)), '2024-08-25 16:35:00');

-- Cancelled 3 - Marko (cancelled business class)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers, created_at)
VALUES (@customer3_id, @flight4_id, '2024-09-10 13:00:00', 'CANCELLED', 1500.00, 1, '2024-09-10 13:00:00');

SET @res_cancel3 = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date, departure_time, arrival_time)
SELECT @res_cancel3, @customer3_id, @flight4_id, '5A', 'BUSINESS', 1500.00, 'CANCELLED', '2024-09-10 13:00:00',
       DATE_ADD(NOW(), INTERVAL 15 DAY),
       DATE_ADD(NOW(), INTERVAL 15 DAY) + INTERVAL 5 HOUR;

SET @ticket_cancel3 = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status, transaction_id, created_at)
VALUES (@ticket_cancel3, 1500.00, 'CREDIT_CARD', '2024-09-10 13:05:00', 'REFUNDED', CONCAT('TXN', FLOOR(RAND() * 1000000)), '2024-09-10 13:05:00');

-- Cancelled 4 - Danilo (cancelled with loyalty points)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers, created_at)
VALUES (@customer1_id, @flight3_id, '2024-09-15 10:00:00', 'CANCELLED', 520.00, 1, '2024-09-15 10:00:00');

SET @res_cancel4 = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date, departure_time, arrival_time)
SELECT @res_cancel4, @customer1_id, @flight3_id, '22B', 'ECONOMY', 520.00, 'CANCELLED', '2024-09-15 10:00:00',
       DATE_ADD(NOW(), INTERVAL 20 DAY),
       DATE_ADD(NOW(), INTERVAL 20 DAY) + INTERVAL 3 HOUR;

SET @ticket_cancel4 = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status, transaction_id, created_at)
VALUES (@ticket_cancel4, 520.00, 'LOYALTY_POINTS', '2024-09-15 10:05:00', 'REFUNDED', CONCAT('TXN', FLOOR(RAND() * 1000000)), '2024-09-15 10:05:00');

-- ========================================
-- NOTIFICATIONS
-- ========================================

INSERT INTO notifications (customer_id, message, notification_type, is_read, created_at)
VALUES 
-- Flight completion notifications
(@customer1_id, 'Your flight has been completed! You traveled 2500 km and earned 2500 loyalty points. Safe travels!', 'FLIGHT_COMPLETED', FALSE, '2024-08-05 18:00:00'),
(@customer2_id, 'Your flight has been completed! You traveled 3200 km and earned 3200 loyalty points. Thank you for flying with us!', 'FLIGHT_COMPLETED', TRUE, '2024-07-15 20:30:00'),
(@customer3_id, 'Your flight has been completed! You traveled 1800 km and earned 1800 loyalty points per passenger. We hope you enjoyed your journey!', 'FLIGHT_COMPLETED', FALSE, '2024-09-01 16:45:00'),

-- Cancellation notifications
(@customer1_id, 'Your reservation has been cancelled. Refund of $550.00 has been processed to your original payment method.', 'BOOKING_CANCELLED', TRUE, '2024-09-20 12:00:00'),
(@customer2_id, 'Your reservation has been cancelled. Refund of $480.00 has been processed to your original payment method.', 'BOOKING_CANCELLED', TRUE, '2024-08-25 17:00:00'),
(@customer3_id, 'Your business class reservation has been cancelled. Refund of $1,500.00 has been processed to your credit card.', 'BOOKING_CANCELLED', FALSE, '2024-09-10 14:00:00'),
(@customer1_id, 'Your reservation has been cancelled. 52,000 loyalty points have been refunded to your account.', 'BOOKING_CANCELLED', TRUE, '2024-09-15 11:00:00'),

-- Loyalty tier upgrade notifications
(@customer2_id, 'Congratulations! You have been upgraded to GOLD tier. Enjoy exclusive benefits and priority boarding.', 'LOYALTY_UPDATE', TRUE, '2024-08-01 09:00:00'),
(@customer1_id, 'Congratulations! You have reached SILVER tier. Experience enhanced services and benefits.', 'LOYALTY_UPDATE', FALSE, '2024-08-20 10:00:00'),

-- Payment confirmation notifications
(@customer1_id, 'Payment of $450.00 has been successfully processed for your flight booking.', 'PAYMENT_CONFIRMATION', TRUE, '2024-08-05 10:36:00'),
(@customer2_id, 'Payment of $1,200.00 has been successfully processed for your business class ticket.', 'PAYMENT_CONFIRMATION', TRUE, '2024-07-15 14:26:00'),
(@customer3_id, 'Payment of $1,350.00 has been successfully processed for 3 tickets.', 'PAYMENT_CONFIRMATION', TRUE, '2024-09-01 09:06:00'),

-- Upcoming flight reminders
(@customer1_id, 'Reminder: Your flight departs in 24 hours. Please arrive at the airport 2 hours before departure.', 'FLIGHT_REMINDER', FALSE, NOW() - INTERVAL 2 DAY),
(@customer2_id, 'Reminder: Your flight departs tomorrow. Check-in is now available online.', 'FLIGHT_REMINDER', FALSE, NOW() - INTERVAL 1 DAY),
(@customer3_id, 'Reminder: Don''t forget to check-in online for your upcoming flight to save time at the airport.', 'FLIGHT_REMINDER', FALSE, NOW() - INTERVAL 3 DAY),

-- Special offers
(@customer1_id, 'Special offer: Get 20% off on your next booking! Use code SUMMER2024 at checkout.', 'SPECIAL_OFFER', FALSE, '2024-09-25 08:00:00'),
(@customer2_id, 'Exclusive Gold member offer: Book now and earn double loyalty points on international flights!', 'SPECIAL_OFFER', FALSE, '2024-09-28 09:00:00'),
(@customer3_id, 'Limited time offer: Family packages with up to 30% discount. Perfect for your next vacation!', 'SPECIAL_OFFER', FALSE, '2024-09-30 10:00:00'),

-- Welcome notifications
(@customer1_id, 'Welcome to our airline loyalty program! Start earning points with every flight.', 'INFO', TRUE, '2024-01-10 12:00:00'),
(@customer2_id, 'Thank you for choosing our airline. Your satisfaction is our priority.', 'INFO', TRUE, '2024-02-15 15:00:00'),
(@customer3_id, 'Welcome aboard! Discover exclusive travel benefits and rewards.', 'INFO', TRUE, '2024-03-20 10:00:00');

-- Add some ancillary services if they don't exist
INSERT IGNORE INTO ancillaries (name, description, price, ancillary_type, created_at)
VALUES 
('Extra Baggage 23kg', 'Additional checked baggage allowance', 50.00, 'BAGGAGE', NOW()),
('Priority Boarding', 'Board the aircraft before general boarding', 25.00, 'SERVICE', NOW()),
('Meal Selection', 'Pre-order your preferred meal', 15.00, 'MEAL', NOW()),
('Premium Seat Selection', 'Choose your preferred seat with extra legroom', 30.00, 'SEAT', NOW()),
('Travel Insurance', 'Comprehensive travel coverage', 75.00, 'SERVICE', NOW()),
('Airport Lounge Access', 'Relax in comfort before your flight', 45.00, 'SERVICE', NOW());

-- Summary of inserted data
SELECT 'Presentation data inserted successfully!' AS Status;

SELECT 
    'Summary of Data' AS Info,
    (SELECT COUNT(*) FROM users WHERE user_type='CUSTOMER') AS Total_Customers,
    (SELECT COUNT(*) FROM reservations) AS Total_Reservations,
    (SELECT COUNT(*) FROM tickets WHERE status = 'USED') AS Used_Tickets,
    (SELECT COUNT(*) FROM tickets WHERE status IN ('CANCELLED', 'REFUNDED')) AS Cancelled_Tickets,
    (SELECT COUNT(*) FROM notifications) AS Total_Notifications,
    (SELECT COUNT(*) FROM saved_payment_methods) AS Saved_Payment_Methods,
    (SELECT COUNT(*) FROM payments WHERE payment_status = 'COMPLETED') AS Completed_Payments,
    (SELECT COUNT(*) FROM payments WHERE payment_status = 'REFUNDED') AS Refunded_Payments;
