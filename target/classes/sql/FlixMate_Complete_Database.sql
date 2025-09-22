-- ========================================
-- FlixMate Complete Database Setup
-- ========================================
-- This is the ONLY SQL file you need for FlixMate
-- It creates the complete database with all fixes applied

-- Drop existing database if it exists (optional - remove if you want to keep existing data)
-- DROP DATABASE IF EXISTS FlixMate;
-- GO

-- Create the database
CREATE DATABASE FlixMate;
GO

-- Switch to the new database
USE FlixMate;
GO

-- ========================================
-- 1. USER STATUS TABLE (must be created first)
-- ========================================
CREATE TABLE user_status (
    status_id INT PRIMARY KEY IDENTITY,
    status_name VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL
);
GO

-- Insert default user statuses
INSERT INTO user_status (status_name, role) VALUES 
('USER', 'USER'),
('ADMIN', 'ADMIN');
GO

-- ========================================
-- 2. USERS TABLE
-- ========================================
CREATE TABLE users (
    user_id INT PRIMARY KEY IDENTITY,
    user_name VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    registration_date VARCHAR(50) DEFAULT CONVERT(VARCHAR(50), GETDATE(), 120),
    last_login VARCHAR(50),
    status_id INT NOT NULL FOREIGN KEY REFERENCES user_status(status_id)
);
GO

-- ========================================
-- 3. MOVIES TABLE
-- ========================================
CREATE TABLE movies (
    movie_id INT PRIMARY KEY IDENTITY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    genre VARCHAR(50),
    release_year INT,
    duration INT, -- minutes
    director VARCHAR(100),
    cast TEXT
);
GO

-- ========================================
-- 4. CINEMA HALLS TABLE
-- ========================================
CREATE TABLE cinema_halls (
    hall_id INT PRIMARY KEY IDENTITY,
    name VARCHAR(50),
    capacity INT NOT NULL,
    location VARCHAR(100)
);
GO

-- ========================================
-- 5. SEATS TABLE
-- ========================================
CREATE TABLE seats (
    seat_id INT PRIMARY KEY IDENTITY,
    hall_id INT FOREIGN KEY REFERENCES cinema_halls(hall_id),
    row_letter CHAR(1),
    seat_number INT,
    seat_type VARCHAR(20) DEFAULT 'Standard', -- e.g., Standard, VIP
    status VARCHAR(20) DEFAULT 'Available' -- Available, Booked, Maintenance
);
GO

-- ========================================
-- 6. SHOWTIMES TABLE (FIXED: Uses VARCHAR for better compatibility)
-- ========================================
CREATE TABLE showtimes (
    showtime_id INT PRIMARY KEY IDENTITY,
    movie_id INT FOREIGN KEY REFERENCES movies(movie_id),
    hall_id INT FOREIGN KEY REFERENCES cinema_halls(hall_id),
    start_time VARCHAR(50) NOT NULL,
    end_time VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);
GO

-- ========================================
-- 7. BOOKINGS TABLE (FIXED: Uses VARCHAR for better compatibility)
-- ========================================
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY IDENTITY,
    user_id INT FOREIGN KEY REFERENCES users(user_id),
    showtime_id INT FOREIGN KEY REFERENCES showtimes(showtime_id),
    booking_date VARCHAR(50) DEFAULT CONVERT(VARCHAR(50), GETDATE(), 120),
    total_seats INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending' -- Pending, Confirmed, Cancelled
);
GO

-- ========================================
-- 8. BOOKING SEATS TABLE (many-to-many for bookings and seats)
-- ========================================
CREATE TABLE booking_seats (
    booking_id INT FOREIGN KEY REFERENCES bookings(booking_id),
    seat_id INT FOREIGN KEY REFERENCES seats(seat_id),
    reserved_status VARCHAR(20) DEFAULT 'Reserved',
    PRIMARY KEY (booking_id, seat_id)
);
GO

-- ========================================
-- 9. LOYALTY POINTS TABLE
-- ========================================
CREATE TABLE loyalty_points (
    points_id INT PRIMARY KEY IDENTITY,
    user_id INT FOREIGN KEY REFERENCES users(user_id),
    points_balance INT NOT NULL DEFAULT 0,
    total_earned INT NOT NULL DEFAULT 0,
    total_redeemed INT NOT NULL DEFAULT 0,
    last_updated VARCHAR(50) DEFAULT CONVERT(VARCHAR(50), GETDATE(), 120)
);
GO

-- ========================================
-- 10. DISCOUNT CODES TABLE
-- ========================================
CREATE TABLE discount_codes (
    code_id INT PRIMARY KEY IDENTITY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    discount_type VARCHAR(20) NOT NULL, -- PERCENTAGE or FIXED_AMOUNT
    discount_value DECIMAL(10,2) NOT NULL,
    min_purchase_amount DECIMAL(10,2) DEFAULT 0,
    max_discount_amount DECIMAL(10,2),
    usage_limit INT,
    used_count INT NOT NULL DEFAULT 0,
    valid_from VARCHAR(50) NOT NULL,
    valid_until VARCHAR(50) NOT NULL,
    is_active BIT NOT NULL DEFAULT 1
);
GO

-- ========================================
-- 11. PAYMENTS TABLE (FIXED: Uses VARCHAR for better compatibility)
-- ========================================
CREATE TABLE payments (
    payment_id INT PRIMARY KEY IDENTITY,
    booking_id INT FOREIGN KEY REFERENCES bookings(booking_id),
    amount DECIMAL(10,2) NOT NULL,
    payment_date VARCHAR(50) DEFAULT CONVERT(VARCHAR(50), GETDATE(), 120),
    payment_method VARCHAR(50), -- e.g., Card, Wallet
    transaction_id VARCHAR(100),
    status VARCHAR(20) DEFAULT 'Pending' -- Pending, Completed, Failed
);
GO

-- ========================================
-- 12. REVIEWS TABLE (FIXED: Uses VARCHAR for better compatibility)
-- ========================================
CREATE TABLE reviews (
    review_id INT PRIMARY KEY IDENTITY,
    user_id INT FOREIGN KEY REFERENCES users(user_id),
    movie_id INT FOREIGN KEY REFERENCES movies(movie_id),
    rating INT CHECK (rating BETWEEN 1 AND 5),
    title VARCHAR(200),
    comment TEXT,
    review_date VARCHAR(50) DEFAULT CONVERT(VARCHAR(50), GETDATE(), 120)
);
GO

-- ========================================
-- 13. REPORTS TABLE (FIXED: Uses VARCHAR for better compatibility)
-- ========================================
CREATE TABLE reports (
    report_id INT PRIMARY KEY IDENTITY,
    user_id INT FOREIGN KEY REFERENCES users(user_id),
    generated_date VARCHAR(50) DEFAULT CONVERT(VARCHAR(50), GETDATE(), 120),
    type VARCHAR(50) -- e.g., Revenue, Popularity, Ticket_Sales
);
GO

-- ========================================
-- 14. STAFF SCHEDULES TABLE (FIXED: Uses VARCHAR for better compatibility)
-- ========================================
CREATE TABLE staff_schedules (
    schedule_id INT PRIMARY KEY IDENTITY,
    user_id INT FOREIGN KEY REFERENCES users(user_id),
    hall_id INT FOREIGN KEY REFERENCES cinema_halls(hall_id),
    start_time VARCHAR(50) NOT NULL,
    end_time VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL
);
GO

-- ========================================
-- 15. INDEXES FOR PERFORMANCE
-- ========================================
CREATE INDEX IDX_ShowTime_StartTime ON showtimes(start_time);
CREATE INDEX IDX_Booking_Status ON bookings(status);
CREATE INDEX IDX_User_Email ON users(email);
CREATE INDEX IDX_Movie_Title ON movies(title);
GO

-- ========================================
-- 16. SAMPLE DATA
-- ========================================

-- Insert sample movies
INSERT INTO movies (title, description, genre, release_year, duration, director, cast) VALUES
('The Matrix', 'A computer hacker learns about the true nature of reality', 'Sci-Fi', 1999, 136, 'Lana Wachowski', 'Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss'),
('Inception', 'A thief who steals corporate secrets through dream-sharing technology', 'Sci-Fi', 2010, 148, 'Christopher Nolan', 'Leonardo DiCaprio, Marion Cotillard, Tom Hardy'),
('The Dark Knight', 'Batman faces the Joker in Gotham City', 'Action', 2008, 152, 'Christopher Nolan', 'Christian Bale, Heath Ledger, Aaron Eckhart'),
('Avatar', 'A paraplegic marine dispatched to the moon Pandora on a unique mission', 'Sci-Fi', 2009, 162, 'James Cameron', 'Sam Worthington, Zoe Saldana, Sigourney Weaver'),
('Titanic', 'A seventeen-year-old aristocrat falls in love with a kind but poor artist', 'Romance', 1997, 194, 'James Cameron', 'Leonardo DiCaprio, Kate Winslet, Billy Zane');
GO

-- Insert sample cinema halls
INSERT INTO cinema_halls (name, capacity, location) VALUES
('Hall A', 100, 'Ground Floor'),
('Hall B', 150, 'First Floor'),
('Hall C', 200, 'Second Floor'),
('Hall D', 120, 'Ground Floor'),
('Hall E', 180, 'First Floor');
GO

-- Insert sample seats for all halls
-- Hall A (100 seats)
INSERT INTO seats (hall_id, row_letter, seat_number, seat_type, status) 
SELECT 1, CHAR(65 + (number-1)/10), ((number-1) % 10) + 1, 'Standard', 'Available'
FROM (SELECT TOP 100 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) as number FROM sys.objects) t;
GO

-- Hall B (150 seats)
INSERT INTO seats (hall_id, row_letter, seat_number, seat_type, status) 
SELECT 2, CHAR(65 + (number-1)/15), ((number-1) % 15) + 1, 'Standard', 'Available'
FROM (SELECT TOP 150 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) as number FROM sys.objects) t;
GO

-- Hall C (200 seats)
INSERT INTO seats (hall_id, row_letter, seat_number, seat_type, status) 
SELECT 3, CHAR(65 + (number-1)/20), ((number-1) % 20) + 1, 'Standard', 'Available'
FROM (SELECT TOP 200 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) as number FROM sys.objects) t;
GO

-- Hall D (120 seats)
INSERT INTO seats (hall_id, row_letter, seat_number, seat_type, status) 
SELECT 4, CHAR(65 + (number-1)/12), ((number-1) % 12) + 1, 'Standard', 'Available'
FROM (SELECT TOP 120 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) as number FROM sys.objects) t;
GO

-- Hall E (180 seats)
INSERT INTO seats (hall_id, row_letter, seat_number, seat_type, status) 
SELECT 5, CHAR(65 + (number-1)/18), ((number-1) % 18) + 1, 'Standard', 'Available'
FROM (SELECT TOP 180 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) as number FROM sys.objects) t;
GO

-- Insert sample showtimes (FIXED: Using future dates after 2025/9/17)
INSERT INTO showtimes (movie_id, hall_id, start_time, end_time, price) VALUES
-- The Matrix showtimes
(1, 1, '2025-09-18T14:00:00', '2025-09-18T16:16:00', 10.00),
(1, 1, '2025-09-18T18:00:00', '2025-09-18T20:16:00', 12.00),
(1, 2, '2025-09-18T20:30:00', '2025-09-18T22:46:00', 15.00),

-- Inception showtimes
(2, 2, '2025-09-19T15:00:00', '2025-09-19T17:28:00', 15.00),
(2, 3, '2025-09-19T19:00:00', '2025-09-19T21:28:00', 18.00),
(2, 1, '2025-09-19T21:30:00', '2025-09-19T23:58:00', 20.00),

-- The Dark Knight showtimes
(3, 3, '2025-09-20T16:00:00', '2025-09-20T18:32:00', 18.00),
(3, 4, '2025-09-20T20:00:00', '2025-09-20T22:32:00', 20.00),

-- Avatar showtimes
(4, 4, '2025-09-21T13:00:00', '2025-09-21T15:42:00', 25.00),
(4, 5, '2025-09-21T17:00:00', '2025-09-21T19:42:00', 28.00),

-- Titanic showtimes
(5, 5, '2025-09-22T14:30:00', '2025-09-22T17:44:00', 22.00),
(5, 1, '2025-09-22T19:30:00', '2025-09-22T22:44:00', 25.00);
GO

-- Insert sample admin user
INSERT INTO users (user_name, password_hash, email, phone, status_id) VALUES
('Admin User', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjT8k2h9.8K9K9K9K9K9K9K', 'admin@flixmate.com', '1234567890', 2),
('Test User', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjT8k2h9.8K9K9K9K9K9K9K', 'test@flixmate.com', '0987654321', 1);
GO

-- Insert sample loyalty points
INSERT INTO loyalty_points (user_id, points_balance, total_earned, total_redeemed) VALUES
(1, 500, 1000, 500),
(2, 250, 500, 250);
GO

-- Insert sample discount codes
INSERT INTO discount_codes (code, description, discount_type, discount_value, min_purchase_amount, max_discount_amount, usage_limit, valid_from, valid_until, is_active) VALUES
('WELCOME10', 'Welcome discount for new users', 'PERCENTAGE', 10.00, 20.00, 5.00, 100, '2025-01-01T00:00:00', '2025-12-31T23:59:59', 1),
('SAVE5', 'Fixed amount discount', 'FIXED_AMOUNT', 5.00, 15.00, NULL, 50, '2025-01-01T00:00:00', '2025-12-31T23:59:59', 1),
('VIP20', 'VIP member discount', 'PERCENTAGE', 20.00, 50.00, 15.00, 25, '2025-01-01T00:00:00', '2025-12-31T23:59:59', 1);
GO

-- Insert sample staff schedules (FIXED: Using future dates after 2025/9/17)
INSERT INTO staff_schedules (user_id, hall_id, start_time, end_time, role) VALUES
(1, 1, '2025-09-18T18:00:00', '2025-09-18T22:00:00', 'Manager'),
(1, 1, '2025-09-18T19:00:00', '2025-09-18T23:00:00', 'Staff'),
(1, 2, '2025-09-19T14:00:00', '2025-09-19T18:00:00', 'Staff');
GO

-- ========================================
-- 17. VERIFICATION QUERIES
-- ========================================
PRINT '========================================';
PRINT 'FlixMate Database Created Successfully!';
PRINT '========================================';
PRINT '';

-- Verify all tables were created
SELECT 'Tables Created:' as Status, COUNT(*) as Count FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';
PRINT '';

-- Verify sample data
SELECT 'Movies:' as Table, COUNT(*) as Count FROM movies;
SELECT 'Cinema Halls:' as Table, COUNT(*) as Count FROM cinema_halls;
SELECT 'Seats:' as Table, COUNT(*) as Count FROM seats;
SELECT 'Showtimes:' as Table, COUNT(*) as Count FROM showtimes;
SELECT 'Users:' as Table, COUNT(*) as Count FROM users;
PRINT '';

-- Verify timestamp columns are DATETIME (not DATETIME2)
SELECT 'Timestamp Columns Check:' as Status;
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE (COLUMN_NAME LIKE '%time%' OR COLUMN_NAME LIKE '%date%') 
AND DATA_TYPE = 'datetime'
ORDER BY TABLE_NAME, COLUMN_NAME;
PRINT '';

PRINT '========================================';
PRINT 'Database setup completed successfully!';
PRINT 'You can now start your FlixMate application.';
PRINT '========================================';
