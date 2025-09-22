@echo off
echo ========================================
echo FlixMate Application Starter
echo ========================================
echo.

echo Step 1: Stopping any running application...
taskkill /f /im java.exe 2>nul
timeout /t 2 /nobreak >nul

echo Step 2: Compiling the application...
call mvnw.cmd clean compile
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    echo Please check Java installation and try again.
    pause
    exit /b 1
)

echo Step 3: Starting the application...
echo The application will start in the background.
echo Check http://localhost:8080 to test the application.
echo.

start /b mvnw.cmd spring-boot:run

echo.
echo ========================================
echo Application Started!
echo ========================================
echo.
echo IMPORTANT: Before testing the application, you MUST run the database setup:
echo.
echo 1. Open SQL Server Management Studio
echo 2. Execute the file: FlixMate_Complete_Database.sql
echo 3. This will fix all timestamp conversion issues
echo.
echo After running the database script, test these features:
echo - Movies: http://localhost:8080/movies.html
echo - Booking: http://localhost:8080/booking.html
echo - Payment: http://localhost:8080/payment.html
echo.
echo Press any key to exit...
pause >nul
