@echo off
echo ========================================
echo FlixMate Java Setup and Testing
echo ========================================
echo.

REM Check if Java is available in PATH
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo Java found in PATH:
    java -version
    echo.
    goto :run_tests
)

echo Java not found in PATH. Checking common installation locations...
echo.

REM Check common Java installation paths
set JAVA_PATHS[0]="C:\Program Files\Java\jdk-17\bin\java.exe"
set JAVA_PATHS[1]="C:\Program Files\Java\jdk-21\bin\java.exe"
set JAVA_PATHS[2]="C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot\bin\java.exe"
set JAVA_PATHS[3]="C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot\bin\java.exe"
set JAVA_PATHS[4]="C:\Program Files\OpenJDK\jdk-17\bin\java.exe"
set JAVA_PATHS[5]="C:\Program Files\OpenJDK\jdk-21\bin\java.exe"

for /L %%i in (0,1,5) do (
    call set "JAVA_PATH=%%JAVA_PATHS[%%i]%%"
    if exist !JAVA_PATH! (
        echo Found Java at: !JAVA_PATH!
        set "JAVA_HOME=!JAVA_PATH:~0,-10!"
        set "PATH=!JAVA_PATH:~0,-10!;%PATH%"
        goto :run_tests
    )
)

echo.
echo ========================================
echo JAVA INSTALLATION REQUIRED
echo ========================================
echo.
echo Java JDK 17 or higher is required but not found.
echo.
echo Please install Java from one of these sources:
echo 1. Eclipse Adoptium: https://adoptium.net/
echo 2. Oracle JDK: https://www.oracle.com/java/technologies/downloads/
echo 3. OpenJDK: https://openjdk.org/
echo.
echo After installation, add Java to your PATH or run this script again.
echo.
pause
exit /b 1

:run_tests
echo.
echo ========================================
echo RUNNING CRUD TESTS
echo ========================================
echo.

REM Set JAVA_HOME if not already set
if not defined JAVA_HOME (
    for /f "tokens=*" %%i in ('where java') do (
        set "JAVA_HOME=%%i"
        set "JAVA_HOME=!JAVA_HOME:~0,-10!"
        goto :java_home_set
    )
)
:java_home_set

echo JAVA_HOME: %JAVA_HOME%
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
echo Compilation successful! Running tests...
echo.

REM Run the tests
call mvnw.cmd test -Dtest=FlixMateCrudIntegrationTest -Dspring.profiles.active=test

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ALL TESTS PASSED SUCCESSFULLY!
    echo ========================================
    echo.
    echo Generating test report...
    python generate_test_report.py
    echo.
    echo Test report generated: flixmate_crud_test_report.html
    echo.
) else (
    echo.
    echo ========================================
    echo SOME TESTS FAILED
    echo ========================================
    echo.
    echo Please check the test output above for details.
    echo.
)

echo.
echo Testing completed.
pause
