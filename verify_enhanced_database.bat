@echo off
echo ============================================================================
echo FlixMate Enhanced Database Verification Script
echo ============================================================================
echo.

REM Prompt for SQL Server connection details
set /p server="Enter SQL Server instance (default: localhost): "
if "%server%"=="" set server=localhost

set /p username="Enter username (leave empty for Windows Authentication): "
if "%username%"=="" (
    set auth_mode=-E
    echo Using Windows Authentication
) else (
    set /p password="Enter password: "
    set auth_mode=-U %username% -P %password%
    echo Using SQL Server Authentication
)

echo.
echo Connecting to SQL Server: %server%
echo Database: FLIXMATE_2_0
echo.

REM Check if database exists
echo Checking if database exists...
sqlcmd -S %server% %auth_mode% -Q "IF EXISTS (SELECT name FROM sys.databases WHERE name = 'FLIXMATE_2_0') PRINT 'Database EXISTS' ELSE PRINT 'Database NOT FOUND'"
if errorlevel 1 (
    echo ERROR: Failed to connect to SQL Server
    pause
    exit /b 1
)

echo.
echo Verifying table structure...

REM Create verification SQL
set TEMP_SQL=%TEMP%\verify_flixmate.sql
(
echo USE FLIXMATE_2_0;
echo.
echo PRINT '============================================================================';
echo PRINT 'TABLE VERIFICATION REPORT';
echo PRINT '============================================================================';
echo.
echo PRINT 'Core Tables:';
echo SELECT 'user_status' as TableName, COUNT(^*^) as RecordCount FROM user_status;
echo SELECT 'users' as TableName, COUNT(^*^) as RecordCount FROM users;
echo SELECT 'movies' as TableName, COUNT(^*^) as RecordCount FROM movies;
echo SELECT 'cinema_halls' as TableName, COUNT(^*^) as RecordCount FROM cinema_halls;
echo SELECT 'seats' as TableName, COUNT(^*^) as RecordCount FROM seats;
echo SELECT 'showtimes' as TableName, COUNT(^*^) as RecordCount FROM showtimes;
echo.
echo PRINT 'Business Tables:';
echo SELECT 'bookings' as TableName, COUNT(^*^) as RecordCount FROM bookings;
echo SELECT 'payments' as TableName, COUNT(^*^) as RecordCount FROM payments;
echo SELECT 'reviews' as TableName, COUNT(^*^) as RecordCount FROM reviews;
echo SELECT 'loyalty_points' as TableName, COUNT(^*^) as RecordCount FROM loyalty_points;
echo SELECT 'discount_codes' as TableName, COUNT(^*^) as RecordCount FROM discount_codes;
echo SELECT 'promotional_banners' as TableName, COUNT(^*^) as RecordCount FROM promotional_banners;
echo SELECT 'reports' as TableName, COUNT(^*^) as RecordCount FROM reports;
echo SELECT 'staff_schedules' as TableName, COUNT(^*^) as RecordCount FROM staff_schedules;
echo.
echo PRINT 'Views:';
echo SELECT 'vw_active_movies_with_showtimes' as ViewName, COUNT(^*^) as RecordCount FROM vw_active_movies_with_showtimes;
echo SELECT 'vw_booking_details' as ViewName, COUNT(^*^) as RecordCount FROM vw_booking_details;
echo.
echo PRINT 'Sample Data Verification:';
echo PRINT 'Admin User:';
echo SELECT user_name, email, s.status_name, s.role 
echo FROM users u 
echo JOIN user_status s ON u.status_id = s.status_id 
echo WHERE u.email = 'admin@flixmate.com';
echo.
echo PRINT 'Available Movies:';
echo SELECT title, genre, duration, rating FROM movies WHERE is_active = 1;
echo.
echo PRINT 'Cinema Halls:';
echo SELECT name, location, capacity, screen_type, sound_system FROM cinema_halls WHERE is_active = 1;
echo.
echo PRINT 'Today''s Showtimes:';
echo SELECT m.title, ch.name as hall, st.start_time, st.price 
echo FROM showtimes st
echo JOIN movies m ON st.movie_id = m.movie_id
echo JOIN cinema_halls ch ON st.hall_id = ch.hall_id
echo WHERE st.is_active = 1
echo ORDER BY st.start_time;
echo.
echo PRINT 'Available Discount Codes:';
echo SELECT code, description, discount_type, discount_value FROM discount_codes WHERE is_active = 1;
echo.
echo PRINT '============================================================================';
echo PRINT 'VERIFICATION COMPLETED SUCCESSFULLY!';
echo PRINT '============================================================================';
) > %TEMP_SQL%

REM Execute verification
sqlcmd -S %server% %auth_mode% -i %TEMP_SQL%
if errorlevel 1 (
    echo ERROR: Verification failed
    del %TEMP_SQL%
    pause
    exit /b 1
)

REM Clean up
del %TEMP_SQL%

echo.
echo ============================================================================
echo Database verification completed successfully!
echo The enhanced FlixMate database is ready for use.
echo ============================================================================
echo.
echo Press any key to exit...
pause >nul
exit /b 0
