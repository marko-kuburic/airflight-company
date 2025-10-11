-- Presentation Data for Air Company System
-- This script adds realistic data for demonstration purposes

USE air_company;

-- Add more customers for testing
INSERT INTO customers (first_name, last_name, email, password, phone_number, date_of_birth, passport_number, nationality, created_at)
VALUES 
('John', 'Smith', 'john.smith@email.com', '$2a$10$dummyHashedPassword1234567890', '+1234567890', '1985-03-15', 'US123456', 'American', '2024-01-10 10:00:00'),
('Emma', 'Johnson', 'emma.j@email.com', '$2a$10$dummyHashedPassword1234567890', '+1234567891', '1990-07-22', 'UK654321', 'British', '2024-02-15 14:30:00'),
('Pierre', 'Dubois', 'pierre.d@email.com', '$2a$10$dummyHashedPassword1234567890', '+33123456789', '1988-11-08', 'FR789012', 'French', '2024-03-20 09:15:00'),
('Maria', 'Garcia', 'maria.g@email.com', '$2a$10$dummyHashedPassword1234567890', '+34987654321', '1992-05-30', 'ES345678', 'Spanish', '2024-04-05 16:45:00'),
('Hans', 'Mueller', 'hans.m@email.com', '$2a$10$dummyHashedPassword1234567890', '+49123456789', '1987-09-12', 'DE901234', 'German', '2024-05-12 11:20:00');

-- Get customer IDs for reference (assuming they start from ID 3 since we already have 2)
SET @customer1_id = (SELECT customer_id FROM customers WHERE email = 'john.smith@email.com');
SET @customer2_id = (SELECT customer_id FROM customers WHERE email = 'emma.j@email.com');
SET @customer3_id = (SELECT customer_id FROM customers WHERE email = 'pierre.d@email.com');
SET @customer4_id = (SELECT customer_id FROM customers WHERE email = 'maria.g@email.com');
SET @customer5_id = (SELECT customer_id FROM customers WHERE email = 'hans.m@email.com');

-- Add Loyalty Programs for all customers
INSERT INTO loyalty (customer_id, points, tier, joined_date)
VALUES 
(@customer1_id, 12500, 'SILVER', '2024-01-10'),
(@customer2_id, 35000, 'GOLD', '2024-02-15'),
(@customer3_id, 8500, 'BRONZE', '2024-03-20'),
(@customer4_id, 65000, 'PLATINUM', '2024-04-05'),
(@customer5_id, 125000, 'DIAMOND', '2024-05-12');

-- Add saved Payment Methods for customers
INSERT INTO payment_method (customer_id, card_number, card_holder_name, expiry_date, cvv, is_default)
VALUES 
(@customer1_id, '4532123456789012', 'JOHN SMITH', '2026-12-31', '123', TRUE),
(@customer1_id, '5425123456789012', 'JOHN SMITH', '2027-06-30', '456', FALSE),
(@customer2_id, '4916123456789012', 'EMMA JOHNSON', '2026-08-31', '789', TRUE),
(@customer3_id, '4539123456789012', 'PIERRE DUBOIS', '2027-03-31', '234', TRUE),
(@customer4_id, '5412123456789012', 'MARIA GARCIA', '2026-11-30', '567', TRUE),
(@customer5_id, '4485123456789012', 'HANS MUELLER', '2027-09-30', '890', TRUE);

-- Get some flight IDs for creating past reservations
SET @flight1_id = (SELECT flight_id FROM flights ORDER BY RAND() LIMIT 1);
SET @flight2_id = (SELECT flight_id FROM flights WHERE flight_id != @flight1_id ORDER BY RAND() LIMIT 1);
SET @flight3_id = (SELECT flight_id FROM flights WHERE flight_id NOT IN (@flight1_id, @flight2_id) ORDER BY RAND() LIMIT 1);

-- Create PAST reservations with USED tickets (completed flights from the past)
-- Past Reservation 1 - John Smith (used ticket from 2 months ago)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers)
VALUES (@customer1_id, @flight1_id, '2024-08-05 10:30:00', 'CONFIRMED', 450.00, 1);

SET @reservation1_id = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date)
VALUES (@reservation1_id, @customer1_id, @flight1_id, '12A', 'ECONOMY', 450.00, 'USED', '2024-08-05 10:30:00');

SET @ticket1_id = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status)
VALUES (@ticket1_id, 450.00, 'CREDIT_CARD', '2024-08-05 10:35:00', 'COMPLETED');

-- Past Reservation 2 - Emma Johnson (used business class ticket)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers)
VALUES (@customer2_id, @flight2_id, '2024-07-15 14:20:00', 'CONFIRMED', 1200.00, 1);

SET @reservation2_id = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date)
VALUES (@reservation2_id, @customer2_id, @flight2_id, '3B', 'BUSINESS', 1200.00, 'USED', '2024-07-15 14:20:00');

SET @ticket2_id = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status)
VALUES (@ticket2_id, 1200.00, 'CREDIT_CARD', '2024-07-15 14:25:00', 'COMPLETED');

-- Past Reservation 3 - Pierre with family (multiple passengers, used)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers)
VALUES (@customer3_id, @flight3_id, '2024-09-01 09:00:00', 'CONFIRMED', 1350.00, 3);

SET @reservation3_id = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date)
VALUES 
(@reservation3_id, @customer3_id, @flight3_id, '15A', 'ECONOMY', 450.00, 'USED', '2024-09-01 09:00:00'),
(@reservation3_id, @customer3_id, @flight3_id, '15B', 'ECONOMY', 450.00, 'USED', '2024-09-01 09:00:00'),
(@reservation3_id, @customer3_id, @flight3_id, '15C', 'ECONOMY', 450.00, 'USED', '2024-09-01 09:00:00');

SET @ticket3a_id = LAST_INSERT_ID();
SET @ticket3b_id = @ticket3a_id + 1;
SET @ticket3c_id = @ticket3a_id + 2;

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status)
VALUES 
(@ticket3a_id, 450.00, 'CREDIT_CARD', '2024-09-01 09:05:00', 'COMPLETED'),
(@ticket3b_id, 450.00, 'CREDIT_CARD', '2024-09-01 09:05:00', 'COMPLETED'),
(@ticket3c_id, 450.00, 'CREDIT_CARD', '2024-09-01 09:05:00', 'COMPLETED');

-- Create CANCELLED reservations
-- Cancelled Reservation 1 - John Smith
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers)
VALUES (@customer1_id, @flight2_id, '2024-09-20 11:00:00', 'CANCELLED', 550.00, 1);

SET @cancel_res1_id = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date)
VALUES (@cancel_res1_id, @customer1_id, @flight2_id, '20D', 'ECONOMY', 550.00, 'CANCELLED', '2024-09-20 11:00:00');

SET @cancel_ticket1_id = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status)
VALUES (@cancel_ticket1_id, 550.00, 'CREDIT_CARD', '2024-09-20 11:05:00', 'REFUNDED');

-- Cancelled Reservation 2 - Emma Johnson
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers)
VALUES (@customer2_id, @flight3_id, '2024-08-25 16:30:00', 'CANCELLED', 480.00, 1);

SET @cancel_res2_id = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date)
VALUES (@cancel_res2_id, @customer2_id, @flight3_id, '18F', 'ECONOMY', 480.00, 'REFUNDED', '2024-08-25 16:30:00');

SET @cancel_ticket2_id = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status)
VALUES (@cancel_ticket2_id, 480.00, 'CREDIT_CARD', '2024-08-25 16:35:00', 'REFUNDED');

-- Cancelled Reservation 3 - Maria Garcia (cancelled business class)
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers)
VALUES (@customer4_id, @flight1_id, '2024-09-10 13:00:00', 'CANCELLED', 1500.00, 1);

SET @cancel_res3_id = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date)
VALUES (@cancel_res3_id, @customer4_id, @flight1_id, '5A', 'BUSINESS', 1500.00, 'CANCELLED', '2024-09-10 13:00:00');

SET @cancel_ticket3_id = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status)
VALUES (@cancel_ticket3_id, 1500.00, 'CREDIT_CARD', '2024-09-10 13:05:00', 'REFUNDED');

-- Cancelled Reservation 4 - Hans Mueller
INSERT INTO reservations (customer_id, flight_id, reservation_date, status, total_amount, num_passengers)
VALUES (@customer5_id, @flight2_id, '2024-09-15 10:00:00', 'CANCELLED', 520.00, 1);

SET @cancel_res4_id = LAST_INSERT_ID();

INSERT INTO tickets (reservation_id, customer_id, flight_id, seat_number, cabin_class_name, price, status, issue_date)
VALUES (@cancel_res4_id, @customer5_id, @flight2_id, '22B', 'ECONOMY', 520.00, 'CANCELLED', '2024-09-15 10:00:00');

SET @cancel_ticket4_id = LAST_INSERT_ID();

INSERT INTO payments (ticket_id, amount, payment_method, payment_date, payment_status)
VALUES (@cancel_ticket4_id, 520.00, 'LOYALTY_POINTS', '2024-09-15 10:05:00', 'REFUNDED');

-- Add Notifications for completed flights and other events
INSERT INTO notifications (customer_id, message, notification_type, is_read, created_at)
VALUES 
-- Flight completion notifications
(@customer1_id, 'Your flight has been completed! You traveled 2500 km and earned 2500 loyalty points. Safe travels!', 'FLIGHT_COMPLETED', FALSE, '2024-08-05 18:00:00'),
(@customer2_id, 'Your flight has been completed! You traveled 3200 km and earned 3200 loyalty points. Thank you for flying with us!', 'FLIGHT_COMPLETED', FALSE, '2024-07-15 20:30:00'),
(@customer3_id, 'Your flight has been completed! You traveled 1800 km and earned 1800 loyalty points. We hope you enjoyed your journey!', 'FLIGHT_COMPLETED', FALSE, '2024-09-01 16:45:00'),

-- Cancellation notifications
(@customer1_id, 'Your reservation has been cancelled. Refund of $550.00 has been processed to your original payment method.', 'BOOKING_CANCELLED', TRUE, '2024-09-20 12:00:00'),
(@customer2_id, 'Your reservation has been cancelled. Refund of $480.00 has been processed to your original payment method.', 'BOOKING_CANCELLED', TRUE, '2024-08-25 17:00:00'),
(@customer4_id, 'Your business class reservation has been cancelled. Refund of $1,500.00 has been processed to your credit card.', 'BOOKING_CANCELLED', FALSE, '2024-09-10 14:00:00'),
(@customer5_id, 'Your reservation has been cancelled. 52,000 loyalty points have been refunded to your account.', 'BOOKING_CANCELLED', TRUE, '2024-09-15 11:00:00'),

-- Loyalty tier upgrade notifications
(@customer2_id, 'Congratulations! You have been upgraded to GOLD tier. Enjoy exclusive benefits and priority boarding.', 'LOYALTY_UPDATE', TRUE, '2024-08-01 09:00:00'),
(@customer4_id, 'Congratulations! You have reached PLATINUM tier. Experience premium services and lounge access.', 'LOYALTY_UPDATE', FALSE, '2024-09-05 10:00:00'),
(@customer5_id, 'Congratulations! You are now a DIAMOND member. Welcome to our highest tier with ultimate privileges!', 'LOYALTY_UPDATE', FALSE, '2024-09-18 11:30:00'),

-- Payment confirmation notifications
(@customer1_id, 'Payment of $450.00 has been successfully processed for your flight booking.', 'PAYMENT_CONFIRMATION', TRUE, '2024-08-05 10:36:00'),
(@customer2_id, 'Payment of $1,200.00 has been successfully processed for your business class ticket.', 'PAYMENT_CONFIRMATION', TRUE, '2024-07-15 14:26:00'),
(@customer3_id, 'Payment of $1,350.00 has been successfully processed for 3 tickets.', 'PAYMENT_CONFIRMATION', TRUE, '2024-09-01 09:06:00'),

-- Upcoming flight reminders
(@customer1_id, 'Reminder: Your flight departs in 24 hours. Please arrive at the airport 2 hours before departure.', 'FLIGHT_REMINDER', FALSE, NOW() - INTERVAL 2 DAY),
(@customer2_id, 'Reminder: Your flight departs tomorrow. Check-in is now available online.', 'FLIGHT_REMINDER', FALSE, NOW() - INTERVAL 1 DAY),

-- Special offers
(@customer1_id, 'Special offer: Get 20% off on your next booking! Use code SUMMER2024 at checkout.', 'SPECIAL_OFFER', FALSE, '2024-09-25 08:00:00'),
(@customer2_id, 'Exclusive Gold member offer: Book now and earn double loyalty points on international flights!', 'SPECIAL_OFFER', FALSE, '2024-09-28 09:00:00'),
(@customer4_id, 'Platinum exclusive: Complimentary upgrade to Business class on your next flight!', 'SPECIAL_OFFER', FALSE, '2024-09-30 10:00:00'),
(@customer5_id, 'Diamond member privilege: Access to our new luxury lounge at major airports worldwide!', 'SPECIAL_OFFER', FALSE, '2024-10-01 11:00:00');

-- Add some ancillary services to tickets
INSERT INTO ancillaries (name, description, price, ancillary_type)
VALUES 
('Extra Baggage 23kg', 'Additional checked baggage allowance', 50.00, 'BAGGAGE'),
('Priority Boarding', 'Board the aircraft before general boarding', 25.00, 'SERVICE'),
('Meal Selection', 'Pre-order your preferred meal', 15.00, 'MEAL'),
('Seat Selection Premium', 'Choose your preferred seat', 30.00, 'SEAT');

-- Link some ancillaries to tickets
SET @ancillary_baggage = (SELECT ancillary_id FROM ancillaries WHERE name = 'Extra Baggage 23kg');
SET @ancillary_priority = (SELECT ancillary_id FROM ancillaries WHERE name = 'Priority Boarding');
SET @ancillary_meal = (SELECT ancillary_id FROM ancillaries WHERE name = 'Meal Selection');

-- Add ancillaries to some tickets (using ticket_ancillary junction table if exists)
-- This assumes there's a many-to-many relationship table

-- Update loyalty points based on completed flights
UPDATE loyalty SET points = points + 2500 WHERE customer_id = @customer1_id;
UPDATE loyalty SET points = points + 3200 WHERE customer_id = @customer2_id;
UPDATE loyalty SET points = points + 1800 WHERE customer_id = @customer3_id;

-- Summary message
SELECT 'Presentation data inserted successfully!' AS Status,
       (SELECT COUNT(*) FROM customers) AS Total_Customers,
       (SELECT COUNT(*) FROM reservations) AS Total_Reservations,
       (SELECT COUNT(*) FROM tickets WHERE status = 'USED') AS Used_Tickets,
       (SELECT COUNT(*) FROM tickets WHERE status IN ('CANCELLED', 'REFUNDED')) AS Cancelled_Tickets,
       (SELECT COUNT(*) FROM notifications) AS Total_Notifications,
       (SELECT COUNT(*) FROM payment_method) AS Saved_Payment_Methods;
