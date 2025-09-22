-- ========================================
-- FlixMate Complete Database Setup - Enhanced Version
-- ========================================
-- This is the ONLY SQL file you need for FlixMate
-- It creates the complete database with all fixes applied and enhanced error handling

-- Enable error handling
SET XACT_ABORT ON;
GO

-- Drop existing database if it exists (optional - remove if you want to keep existing data)
-- DROP DATABASE IF EXISTS FlixMate;
-- GO

-- Create the database with error handling
BEGIN TRY
    CREATE DATABASE FlixMate;
    PRINT 'Database FlixMate created successfully';
END TRY
BEGIN CATCH
    IF ERROR_NUMBER() = 1801
        PRINT 'Database FlixMate already exists';
    ELSE
        THROW;
END CATCH
GO

-- Switch to the new database
USE FlixMate;
GO

-- ========================================
-- 1. USER STATUS TABLE (must be created first)
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_status')
    BEGIN
        CREATE TABLE user_status (
            status_id INT PRIMARY KEY IDENTITY(1,1),
            status_name VARCHAR(50) NOT NULL UNIQUE,
            role VARCHAR(50) NOT NULL,
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE()
        );
        PRINT 'Table user_status created successfully';
    END
    ELSE
        PRINT 'Table user_status already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating user_status table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- Insert default user statuses with error handling
BEGIN TRY
    IF NOT EXISTS (SELECT 1 FROM user_status WHERE status_name = 'USER')
    BEGIN
        INSERT INTO user_status (status_name, role) VALUES ('USER', 'USER');
        PRINT 'Default USER status inserted';
    END
    
    IF NOT EXISTS (SELECT 1 FROM user_status WHERE status_name = 'ADMIN')
    BEGIN
        INSERT INTO user_status (status_name, role) VALUES ('ADMIN', 'ADMIN');
        PRINT 'Default ADMIN status inserted';
    END
END TRY
BEGIN CATCH
    PRINT 'Error inserting user statuses: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 2. USERS TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
    BEGIN
        CREATE TABLE users (
            user_id INT PRIMARY KEY IDENTITY(1,1),
            user_name VARCHAR(50) NOT NULL,
            password_hash VARCHAR(255) NOT NULL,
            email VARCHAR(100) UNIQUE NOT NULL,
            phone VARCHAR(20),
            registration_date DATETIME2 DEFAULT GETDATE(),
            last_login DATETIME2,
            status_id INT NOT NULL FOREIGN KEY REFERENCES user_status(status_id),
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE(),
            is_active BIT DEFAULT 1
        );
        PRINT 'Table users created successfully';
    END
    ELSE
        PRINT 'Table users already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating users table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 3. MOVIES TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'movies')
    BEGIN
        CREATE TABLE movies (
            movie_id INT PRIMARY KEY IDENTITY(1,1),
            title VARCHAR(100) NOT NULL,
            description TEXT,
            genre VARCHAR(50),
            release_year INT CHECK (release_year > 1900 AND release_year <= YEAR(GETDATE()) + 5),
            duration INT CHECK (duration > 0 AND duration <= 600), -- minutes, max 10 hours
            director VARCHAR(100),
            cast TEXT,
            language VARCHAR(50) DEFAULT 'English',
            rating VARCHAR(10) DEFAULT 'PG-13',
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE(),
            is_active BIT DEFAULT 1
        );
        PRINT 'Table movies created successfully';
    END
    ELSE
        PRINT 'Table movies already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating movies table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 4. CINEMA HALLS TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'cinema_halls')
    BEGIN
        CREATE TABLE cinema_halls (
            hall_id INT PRIMARY KEY IDENTITY(1,1),
            name VARCHAR(50) NOT NULL,
            capacity INT NOT NULL CHECK (capacity > 0 AND capacity <= 1000),
            location VARCHAR(100),
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE(),
            is_active BIT DEFAULT 1
        );
        PRINT 'Table cinema_halls created successfully';
    END
    ELSE
        PRINT 'Table cinema_halls already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating cinema_halls table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 5. SEATS TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'seats')
    BEGIN
        CREATE TABLE seats (
            seat_id INT PRIMARY KEY IDENTITY(1,1),
            hall_id INT NOT NULL FOREIGN KEY REFERENCES cinema_halls(hall_id),
            row_letter CHAR(1) NOT NULL CHECK (row_letter BETWEEN 'A' AND 'Z'),
            seat_number INT NOT NULL CHECK (seat_number > 0 AND seat_number <= 50),
            seat_type VARCHAR(20) DEFAULT 'Standard' CHECK (seat_type IN ('Standard', 'VIP', 'Premium')),
            status VARCHAR(20) DEFAULT 'Available' CHECK (status IN ('Available', 'Booked', 'Maintenance', 'Reserved')),
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE(),
            UNIQUE(hall_id, row_letter, seat_number)
        );
        PRINT 'Table seats created successfully';
    END
    ELSE
        PRINT 'Table seats already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating seats table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 6. SHOWTIMES TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'showtimes')
    BEGIN
        CREATE TABLE showtimes (
            showtime_id INT PRIMARY KEY IDENTITY(1,1),
            movie_id INT NOT NULL FOREIGN KEY REFERENCES movies(movie_id),
            hall_id INT NOT NULL FOREIGN KEY REFERENCES cinema_halls(hall_id),
            start_time DATETIME2 NOT NULL,
            end_time DATETIME2 NOT NULL,
            price DECIMAL(10,2) NOT NULL CHECK (price >= 0 AND price <= 1000),
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE(),
            is_active BIT DEFAULT 1,
            CHECK (end_time > start_time)
        );
        PRINT 'Table showtimes created successfully';
    END
    ELSE
        PRINT 'Table showtimes already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating showtimes table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 7. BOOKINGS TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'bookings')
    BEGIN
        CREATE TABLE bookings (
            booking_id INT PRIMARY KEY IDENTITY(1,1),
            user_id INT NOT NULL FOREIGN KEY REFERENCES users(user_id),
            showtime_id INT NOT NULL FOREIGN KEY REFERENCES showtimes(showtime_id),
            booking_date DATETIME2 DEFAULT GETDATE(),
            total_seats INT NOT NULL CHECK (total_seats > 0 AND total_seats <= 20),
            total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
            status VARCHAR(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Confirmed', 'Cancelled', 'Completed')),
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE(),
            payment_status VARCHAR(20) DEFAULT 'Pending' CHECK (payment_status IN ('Pending', 'Paid', 'Failed', 'Refunded'))
        );
        PRINT 'Table bookings created successfully';
    END
    ELSE
        PRINT 'Table bookings already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating bookings table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 8. BOOKING SEATS TABLE (many-to-many for bookings and seats)
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'booking_seats')
    BEGIN
        CREATE TABLE booking_seats (
            booking_id INT NOT NULL FOREIGN KEY REFERENCES bookings(booking_id) ON DELETE CASCADE,
            seat_id INT NOT NULL FOREIGN KEY REFERENCES seats(seat_id),
            reserved_status VARCHAR(20) DEFAULT 'Reserved' CHECK (reserved_status IN ('Reserved', 'Confirmed', 'Cancelled')),
            created_at DATETIME2 DEFAULT GETDATE(),
            PRIMARY KEY (booking_id, seat_id)
        );
        PRINT 'Table booking_seats created successfully';
    END
    ELSE
        PRINT 'Table booking_seats already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating booking_seats table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 9. LOYALTY POINTS TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'loyalty_points')
    BEGIN
        CREATE TABLE loyalty_points (
            points_id INT PRIMARY KEY IDENTITY(1,1),
            user_id INT NOT NULL FOREIGN KEY REFERENCES users(user_id),
            points_balance INT NOT NULL DEFAULT 0 CHECK (points_balance >= 0),
            total_earned INT NOT NULL DEFAULT 0 CHECK (total_earned >= 0),
            total_redeemed INT NOT NULL DEFAULT 0 CHECK (total_redeemed >= 0),
            last_updated DATETIME2 DEFAULT GETDATE(),
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE(),
            UNIQUE(user_id)
        );
        PRINT 'Table loyalty_points created successfully';
    END
    ELSE
        PRINT 'Table loyalty_points already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating loyalty_points table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 10. DISCOUNT CODES TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'discount_codes')
    BEGIN
        CREATE TABLE discount_codes (
            code_id INT PRIMARY KEY IDENTITY(1,1),
            code VARCHAR(50) NOT NULL UNIQUE,
            description VARCHAR(255),
            discount_type VARCHAR(20) NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
            discount_value DECIMAL(10,2) NOT NULL CHECK (discount_value > 0),
            min_purchase_amount DECIMAL(10,2) DEFAULT 0 CHECK (min_purchase_amount >= 0),
            max_discount_amount DECIMAL(10,2) CHECK (max_discount_amount > 0),
            usage_limit INT CHECK (usage_limit > 0),
            used_count INT NOT NULL DEFAULT 0 CHECK (used_count >= 0),
            valid_from DATETIME2 NOT NULL,
            valid_until DATETIME2 NOT NULL,
            is_active BIT NOT NULL DEFAULT 1,
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE(),
            CHECK (valid_until > valid_from),
            CHECK (used_count <= usage_limit OR usage_limit IS NULL)
        );
        PRINT 'Table discount_codes created successfully';
    END
    ELSE
        PRINT 'Table discount_codes already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating discount_codes table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 11. PAYMENTS TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'payments')
    BEGIN
        CREATE TABLE payments (
            payment_id INT PRIMARY KEY IDENTITY(1,1),
            booking_id INT NOT NULL FOREIGN KEY REFERENCES bookings(booking_id),
            amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
            payment_date DATETIME2 DEFAULT GETDATE(),
            payment_method VARCHAR(50) CHECK (payment_method IN ('Card', 'Wallet', 'Cash', 'Online')),
            transaction_id VARCHAR(100),
            status VARCHAR(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Completed', 'Failed', 'Refunded')),
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE()
        );
        PRINT 'Table payments created successfully';
    END
    ELSE
        PRINT 'Table payments already exists';
    END
END TRY
BEGIN CATCH
    PRINT 'Error creating payments table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 12. REVIEWS TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'reviews')
    BEGIN
        CREATE TABLE reviews (
            review_id INT PRIMARY KEY IDENTITY(1,1),
            user_id INT NOT NULL FOREIGN KEY REFERENCES users(user_id),
            movie_id INT NOT NULL FOREIGN KEY REFERENCES movies(movie_id),
            rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
            title VARCHAR(200),
            comment TEXT,
            review_date DATETIME2 DEFAULT GETDATE(),
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE(),
            is_approved BIT DEFAULT 0,
            UNIQUE(user_id, movie_id) -- One review per user per movie
        );
        PRINT 'Table reviews created successfully';
    END
    ELSE
        PRINT 'Table reviews already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating reviews table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 13. REPORTS TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'reports')
    BEGIN
        CREATE TABLE reports (
            report_id INT PRIMARY KEY IDENTITY(1,1),
            user_id INT NOT NULL FOREIGN KEY REFERENCES users(user_id),
            generated_date DATETIME2 DEFAULT GETDATE(),
            type VARCHAR(50) NOT NULL CHECK (type IN ('Revenue', 'Popularity', 'Ticket_Sales', 'User_Activity')),
            report_data TEXT,
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE()
        );
        PRINT 'Table reports created successfully';
    END
    ELSE
        PRINT 'Table reports already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating reports table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 14. STAFF SCHEDULES TABLE
-- ========================================
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'staff_schedules')
    BEGIN
        CREATE TABLE staff_schedules (
            schedule_id INT PRIMARY KEY IDENTITY(1,1),
            user_id INT NOT NULL FOREIGN KEY REFERENCES users(user_id),
            hall_id INT NOT NULL FOREIGN KEY REFERENCES cinema_halls(hall_id),
            start_time DATETIME2 NOT NULL,
            end_time DATETIME2 NOT NULL,
            role VARCHAR(50) NOT NULL CHECK (role IN ('Manager', 'Staff', 'Security', 'Cleaner')),
            created_at DATETIME2 DEFAULT GETDATE(),
            updated_at DATETIME2 DEFAULT GETDATE(),
            is_active BIT DEFAULT 1,
            CHECK (end_time > start_time)
        );
        PRINT 'Table staff_schedules created successfully';
    END
    ELSE
        PRINT 'Table staff_schedules already exists';
END TRY
BEGIN CATCH
    PRINT 'Error creating staff_schedules table: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 15. INDEXES FOR PERFORMANCE
-- ========================================
BEGIN TRY
    -- Create indexes for better performance
    IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IDX_ShowTime_StartTime')
        CREATE INDEX IDX_ShowTime_StartTime ON showtimes(start_time);
    
    IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IDX_Booking_Status')
        CREATE INDEX IDX_Booking_Status ON bookings(status);
    
    IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IDX_User_Email')
        CREATE INDEX IDX_User_Email ON users(email);
    
    IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IDX_Movie_Title')
        CREATE INDEX IDX_Movie_Title ON movies(title);
    
    IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IDX_Seat_Hall_Status')
        CREATE INDEX IDX_Seat_Hall_Status ON seats(hall_id, status);
    
    IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IDX_Booking_User')
        CREATE INDEX IDX_Booking_User ON bookings(user_id);
    
    IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IDX_ShowTime_Movie')
        CREATE INDEX IDX_ShowTime_Movie ON showtimes(movie_id);
    
    PRINT 'Performance indexes created successfully';
END TRY
BEGIN CATCH
    PRINT 'Error creating indexes: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- ========================================
-- 16. SAMPLE DATA WITH ERROR HANDLING
-- ========================================

-- Insert sample movies with error handling
BEGIN TRY
    IF NOT EXISTS (SELECT 1 FROM movies WHERE title = 'The Matrix')
    BEGIN
        INSERT INTO movies (title, description, genre, release_year, duration, director, cast, language, rating) VALUES
        ('The Matrix', 'A computer hacker learns about the true nature of reality', 'Sci-Fi', 1999, 136, 'Lana Wachowski', 'Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss', 'English', 'R'),
        ('Inception', 'A thief who steals corporate secrets through dream-sharing technology', 'Sci-Fi', 2010, 148, 'Christopher Nolan', 'Leonardo DiCaprio, Marion Cotillard, Tom Hardy', 'English', 'PG-13'),
        ('The Dark Knight', 'Batman faces the Joker in Gotham City', 'Action', 2008, 152, 'Christopher Nolan', 'Christian Bale, Heath Ledger, Aaron Eckhart', 'English', 'PG-13'),
        ('Avatar', 'A paraplegic marine dispatched to the moon Pandora on a unique mission', 'Sci-Fi', 2009, 162, 'James Cameron', 'Sam Worthington, Zoe Saldana, Sigourney Weaver', 'English', 'PG-13'),
        ('Titanic', 'A seventeen-year-old aristocrat falls in love with a kind but poor artist', 'Romance', 1997, 194, 'James Cameron', 'Leonardo DiCaprio, Kate Winslet, Billy Zane', 'English', 'PG-13');
        PRINT 'Sample movies inserted successfully';
    END
    ELSE
        PRINT 'Sample movies already exist';
END TRY
BEGIN CATCH
    PRINT 'Error inserting sample movies: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- Insert sample cinema halls with error handling
BEGIN TRY
    IF NOT EXISTS (SELECT 1 FROM cinema_halls WHERE name = 'Hall A')
    BEGIN
        INSERT INTO cinema_halls (name, capacity, location) VALUES
        ('Hall A', 100, 'Ground Floor'),
        ('Hall B', 150, 'First Floor'),
        ('Hall C', 200, 'Second Floor'),
        ('Hall D', 120, 'Ground Floor'),
        ('Hall E', 180, 'First Floor');
        PRINT 'Sample cinema halls inserted successfully';
    END
    ELSE
        PRINT 'Sample cinema halls already exist';
END TRY
BEGIN CATCH
    PRINT 'Error inserting sample cinema halls: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- Insert sample seats with error handling
BEGIN TRY
    IF NOT EXISTS (SELECT 1 FROM seats WHERE hall_id = 1)
    BEGIN
        -- Hall A (100 seats)
        INSERT INTO seats (hall_id, row_letter, seat_number, seat_type, status) 
        SELECT 1, CHAR(65 + (number-1)/10), ((number-1) % 10) + 1, 'Standard', 'Available'
        FROM (SELECT TOP 100 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) as number FROM sys.objects) t;
        
        -- Hall B (150 seats)
        INSERT INTO seats (hall_id, row_letter, seat_number, seat_type, status) 
        SELECT 2, CHAR(65 + (number-1)/15), ((number-1) % 15) + 1, 'Standard', 'Available'
        FROM (SELECT TOP 150 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) as number FROM sys.objects) t;
        
        -- Hall C (200 seats)
        INSERT INTO seats (hall_id, row_letter, seat_number, seat_type, status) 
        SELECT 3, CHAR(65 + (number-1)/20), ((number-1) % 20) + 1, 'Standard', 'Available'
        FROM (SELECT TOP 200 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) as number FROM sys.objects) t;
        
        -- Hall D (120 seats)
        INSERT INTO seats (hall_id, row_letter, seat_number, seat_type, status) 
        SELECT 4, CHAR(65 + (number-1)/12), ((number-1) % 12) + 1, 'Standard', 'Available'
        FROM (SELECT TOP 120 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) as number FROM sys.objects) t;
        
        -- Hall E (180 seats)
        INSERT INTO seats (hall_id, row_letter, seat_number, seat_type, status) 
        SELECT 5, CHAR(65 + (number-1)/18), ((number-1) % 18) + 1, 'Standard', 'Available'
        FROM (SELECT TOP 180 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) as number FROM sys.objects) t;
        
        PRINT 'Sample seats inserted successfully';
    END
    ELSE
        PRINT 'Sample seats already exist';
END TRY
BEGIN CATCH
    PRINT 'Error inserting sample seats: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- Insert sample showtimes with error handling
BEGIN TRY
    IF NOT EXISTS (SELECT 1 FROM showtimes WHERE movie_id = 1)
    BEGIN
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
        PRINT 'Sample showtimes inserted successfully';
    END
    ELSE
        PRINT 'Sample showtimes already exist';
END TRY
BEGIN CATCH
    PRINT 'Error inserting sample showtimes: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- Insert sample admin user with error handling
BEGIN TRY
    IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@flixmate.com')
    BEGIN
        INSERT INTO users (user_name, password_hash, email, phone, status_id) VALUES
        ('Admin User', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjT8k2h9.8K9K9K9K9K9K9K', 'admin@flixmate.com', '1234567890', 2),
        ('Test User', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFjT8k2h9.8K9K9K9K9K9K9K', 'test@flixmate.com', '0987654321', 1);
        PRINT 'Sample users inserted successfully';
    END
    ELSE
        PRINT 'Sample users already exist';
END TRY
BEGIN CATCH
    PRINT 'Error inserting sample users: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- Insert sample loyalty points with error handling
BEGIN TRY
    IF NOT EXISTS (SELECT 1 FROM loyalty_points WHERE user_id = 1)
    BEGIN
        INSERT INTO loyalty_points (user_id, points_balance, total_earned, total_redeemed) VALUES
        (1, 500, 1000, 500),
        (2, 250, 500, 250);
        PRINT 'Sample loyalty points inserted successfully';
    END
    ELSE
        PRINT 'Sample loyalty points already exist';
END TRY
BEGIN CATCH
    PRINT 'Error inserting sample loyalty points: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- Insert sample discount codes with error handling
BEGIN TRY
    IF NOT EXISTS (SELECT 1 FROM discount_codes WHERE code = 'WELCOME10')
    BEGIN
        INSERT INTO discount_codes (code, description, discount_type, discount_value, min_purchase_amount, max_discount_amount, usage_limit, valid_from, valid_until, is_active) VALUES
        ('WELCOME10', 'Welcome discount for new users', 'PERCENTAGE', 10.00, 20.00, 5.00, 100, '2025-01-01T00:00:00', '2025-12-31T23:59:59', 1),
        ('SAVE5', 'Fixed amount discount', 'FIXED_AMOUNT', 5.00, 15.00, NULL, 50, '2025-01-01T00:00:00', '2025-12-31T23:59:59', 1),
        ('VIP20', 'VIP member discount', 'PERCENTAGE', 20.00, 50.00, 15.00, 25, '2025-01-01T00:00:00', '2025-12-31T23:59:59', 1);
        PRINT 'Sample discount codes inserted successfully';
    END
    ELSE
        PRINT 'Sample discount codes already exist';
END TRY
BEGIN CATCH
    PRINT 'Error inserting sample discount codes: ' + ERROR_MESSAGE();
    THROW;
END CATCH
GO

-- Insert sample staff schedules with error handling
BEGIN TRY
    IF NOT EXISTS (SELECT 1 FROM staff_schedules WHERE user_id = 1)
    BEGIN
        INSERT INTO staff_schedules (user_id, hall_id, start_time, end_time, role) VALUES
        (1, 1, '2025-09-18T18:00:00', '2025-09-18T22:00:00', 'Manager'),
        (1, 1, '2025-09-18T19:00:00', '2025-09-18T23:00:00', 'Staff'),
        (1, 2, '2025-09-19T14:00:00', '2025-09-19T18:00:00', 'Staff');
        PRINT 'Sample staff schedules inserted successfully';
    END
    ELSE
        PRINT 'Sample staff schedules already exist';
END TRY
BEGIN CATCH
    PRINT 'Error inserting sample staff schedules: ' + ERROR_MESSAGE();
    THROW;
END CATCH
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
SELECT 'Loyalty Points:' as Table, COUNT(*) as Count FROM loyalty_points;
SELECT 'Discount Codes:' as Table, COUNT(*) as Count FROM discount_codes;
PRINT '';

-- Verify foreign key constraints
SELECT 'Foreign Key Constraints:' as Status;
SELECT 
    fk.name AS constraint_name,
    tp.name AS parent_table,
    cp.name AS parent_column,
    tr.name AS referenced_table,
    cr.name AS referenced_column
FROM sys.foreign_keys fk
INNER JOIN sys.tables tp ON fk.parent_object_id = tp.object_id
INNER JOIN sys.tables tr ON fk.referenced_object_id = tr.object_id
INNER JOIN sys.foreign_key_columns fkc ON fk.object_id = fkc.constraint_object_id
INNER JOIN sys.columns cp ON fkc.parent_column_id = cp.column_id AND fkc.parent_object_id = cp.object_id
INNER JOIN sys.columns cr ON fkc.referenced_column_id = cr.column_id AND fkc.referenced_object_id = cr.object_id
ORDER BY tp.name, fk.name;
PRINT '';

-- Verify indexes
SELECT 'Indexes Created:' as Status;
SELECT 
    t.name AS table_name,
    i.name AS index_name,
    i.type_desc AS index_type
FROM sys.tables t
INNER JOIN sys.indexes i ON t.object_id = i.object_id
WHERE i.name IS NOT NULL AND i.name NOT LIKE 'PK_%'
ORDER BY t.name, i.name;
PRINT '';

PRINT '========================================';
PRINT 'Database setup completed successfully!';
PRINT 'You can now start your FlixMate application.';
PRINT '========================================';
