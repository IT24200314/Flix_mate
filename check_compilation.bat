@echo off
echo ========================================
echo FlixMate Compilation Check
echo ========================================
echo.

REM Try to find Java in common locations
set JAVA_FOUND=0

REM Check if Java is in PATH
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo Java found in PATH
    set JAVA_FOUND=1
    goto :compile
)

REM Check common Java installation paths
for %%p in (
    "C:\Program Files\Java\jdk-17\bin\java.exe"
    "C:\Program Files\Java\jdk-21\bin\java.exe"
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot\bin\java.exe"
    "C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot\bin\java.exe"
    "C:\Program Files\OpenJDK\jdk-17\bin\java.exe"
    "C:\Program Files\OpenJDK\jdk-21\bin\java.exe"
) do (
    if exist %%p (
        echo Found Java at: %%p
        set "JAVA_HOME=%%~dp0"
        set "PATH=%%~dp0;%PATH%"
        set JAVA_FOUND=1
        goto :compile
    )
)

if %JAVA_FOUND% equ 0 (
    echo.
    echo ERROR: Java not found!
    echo.
    echo Please install Java JDK 17 or higher from:
    echo https://adoptium.net/
    echo.
    echo After installation, add Java to your PATH or run setup_java_and_test.bat
    echo.
    pause
    exit /b 1
)

:compile
echo.
echo Compiling project...
echo.

call mvnw.cmd compile

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo COMPILATION SUCCESSFUL!
    echo ========================================
    echo.
    echo All compilation errors have been fixed.
    echo You can now run the tests using setup_java_and_test.bat
    echo.
) else (
    echo.
    echo ========================================
    echo COMPILATION FAILED
    echo ========================================
    echo.
    echo Please check the errors above and fix them.
    echo.
)

pause
