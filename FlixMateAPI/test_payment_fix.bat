@echo off
echo ========================================
echo FlixMate Payment Fix Test
echo ========================================
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 17 or higher and add it to your PATH
    echo.
    echo You can download Java from: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

echo Java found. Starting application...
echo.

REM Compile the project first
echo Compiling project...
call mvnw.cmd compile
if %errorlevel% neq 0 (
    echo.
    echo Compilation failed. Please check the errors above.
    pause
    exit /b 1
)

echo.
echo Compilation successful! Starting application...
echo.

REM Start the application in background
start /B mvnw.cmd spring-boot:run

echo.
echo Application is starting... Please wait 30 seconds for it to initialize.
echo.

REM Wait for application to start
timeout /t 30 /nobreak >nul

echo.
echo Testing payment flow...
echo.

REM Test the showtimes endpoint
echo Testing showtimes endpoint...
curl -s http://localhost:8080/api/showtimes >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Showtimes endpoint is working
) else (
    echo ✗ Showtimes endpoint failed
)

REM Test the movies endpoint
echo Testing movies endpoint...
curl -s http://localhost:8080/api/movies >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Movies endpoint is working
) else (
    echo ✗ Movies endpoint failed
)

echo.
echo ========================================
echo TEST COMPLETED
echo ========================================
echo.
echo You can now:
echo 1. Open http://localhost:8080/debug-showtimes.html in your browser
echo 2. Check available showtimes and test the payment flow
echo 3. Use the debug page to verify the payment fix
echo.
echo The application is running in the background.
echo Press Ctrl+C in the terminal to stop it.
echo.
pause
