@echo off
echo ========================================
echo FlixMate CRUD Testing Suite
echo ========================================
echo.

echo Starting comprehensive CRUD testing...
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

echo Java version:
java -version
echo.

REM Check if Maven wrapper exists
if not exist "mvnw.cmd" (
    echo ERROR: Maven wrapper not found
    echo Please ensure you are in the project root directory
    pause
    exit /b 1
)

echo Running CRUD tests...
echo.

REM Run the comprehensive CRUD tests
call mvnw.cmd test -Dtest=FlixMateCrudIntegrationTest -Dspring.profiles.active=test

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ALL CRUD TESTS PASSED SUCCESSFULLY!
    echo ========================================
    echo.
    echo Test Summary:
    echo - Movie CRUD: PASSED
    echo - Report CRUD: PASSED  
    echo - Payment CRUD: PASSED
    echo - Booking CRUD: PASSED
    echo - Browse CRUD: PASSED
    echo - Review CRUD: PASSED
    echo.
    echo All 6 functions have been tested for complete CRUD operations
    echo with error handling and data integrity verification.
    echo.
) else (
    echo.
    echo ========================================
    echo SOME TESTS FAILED
    echo ========================================
    echo.
    echo Please check the test output above for details.
    echo Review any failed tests and fix the issues.
    echo.
)

echo.
echo Test execution completed.
echo Check the target/surefire-reports directory for detailed test reports.
echo.
pause
