@echo off
echo ============================================================================
echo FlixMate Enhanced Database Setup Script
echo ============================================================================
echo.

set SCRIPT_DIR=%~dp0
set SQL_FILE="%SCRIPT_DIR%src\main\resources\sql\flixmate_enhanced_schema.sql"

echo Checking if SQL file exists...
if not exist %SQL_FILE% (
    echo ERROR: SQL file not found at %SQL_FILE%
    echo Please ensure the flixmate_enhanced_schema.sql file exists.
    pause
    exit /b 1
)

echo SQL file found: %SQL_FILE%
echo.

echo ============================================================================
echo Database Setup Instructions:
echo ============================================================================
echo.
echo 1. Make sure SQL Server is running
echo 2. Open SQL Server Management Studio (SSMS)
echo 3. Connect to your SQL Server instance
echo 4. Execute the following steps:
echo.
echo    a) First, create the database (if it doesn't exist):
echo       CREATE DATABASE FLIXMATE_2_0;
echo.
echo    b) Then execute the enhanced schema file:
echo       File Location: %SQL_FILE%
echo.
echo ============================================================================
echo Automated Setup (Alternative):
echo ============================================================================
echo.

set /p choice="Do you want to attempt automated setup using sqlcmd? (y/n): "
if /i "%choice%"=="y" goto automated_setup
if /i "%choice%"=="yes" goto automated_setup

echo.
echo Manual setup selected. Please follow the instructions above.
echo Press any key to exit...
pause >nul
exit /b 0

:automated_setup
echo.
echo Attempting automated setup using sqlcmd...
echo.

REM Check if sqlcmd is available
sqlcmd -? >nul 2>&1
if errorlevel 1 (
    echo ERROR: sqlcmd is not available or not in PATH
    echo Please install SQL Server Command Line Utilities or use manual setup
    pause
    exit /b 1
)

echo sqlcmd found. Proceeding with automated setup...
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
echo.

REM Test connection
echo Testing connection...
sqlcmd -S %server% %auth_mode% -Q "SELECT @@VERSION" >nul 2>&1
if errorlevel 1 (
    echo ERROR: Failed to connect to SQL Server
    echo Please check your connection details and try again
    pause
    exit /b 1
)

echo Connection successful!
echo.

REM Create database
echo Creating database FLIXMATE_2_0...
sqlcmd -S %server% %auth_mode% -Q "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'FLIXMATE_2_0') CREATE DATABASE FLIXMATE_2_0"
if errorlevel 1 (
    echo ERROR: Failed to create database
    pause
    exit /b 1
)

echo Database created successfully!
echo.

REM Execute the enhanced schema
echo Executing enhanced schema...
sqlcmd -S %server% %auth_mode% -i %SQL_FILE%
if errorlevel 1 (
    echo ERROR: Failed to execute schema
    pause
    exit /b 1
)

echo.
echo ============================================================================
echo SUCCESS: Enhanced Database Setup Completed!
echo ============================================================================
echo.
echo Database: FLIXMATE_2_0
echo Schema: Enhanced FlixMate Schema
echo.
echo Features Installed:
echo - 14 Tables with optimized structure
echo - 20+ Performance indexes
echo - 2 Useful views for common queries
echo - 2 Stored procedures for business logic
echo - 3 Triggers for automated data management
echo - Sample data for testing
echo.
echo Default Admin Account:
echo Username: SE
echo Password: @Emuib0326
echo Email: admin@flixmate.com
echo.
echo The database is now ready for use with the FlixMate application!
echo.
echo Press any key to exit...
pause >nul
exit /b 0
