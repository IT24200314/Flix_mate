@echo off
echo Checking Java installation...

rem Check if java is in PATH
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo Java found in PATH
    goto :run_app
)

rem Check common Java locations
if exist "C:\Program Files\Java" (
    echo Checking Java in Program Files...
    for /d %%d in ("C:\Program Files\Java\*") do (
        if exist "%%d\bin\java.exe" (
            echo Found Java at: %%d
            set "JAVA_HOME=%%d"
            set "PATH=%%d\bin;%PATH%"
            goto :run_app
        )
    )
)

if exist "C:\Program Files (x86)\Java" (
    echo Checking Java in Program Files (x86)...
    for /d %%d in ("C:\Program Files (x86)\Java\*") do (
        if exist "%%d\bin\java.exe" (
            echo Found Java at: %%d
            set "JAVA_HOME=%%d"
            set "PATH=%%d\bin;%PATH%"
            goto :run_app
        )
    )
)

echo ERROR: Java not found!
echo Please install Java JDK 17 or higher and try again.
echo You can download it from: https://adoptium.net/
pause
exit /b 1

:run_app
echo Starting Spring Boot application...
echo JAVA_HOME=%JAVA_HOME%
mvnw.cmd spring-boot:run
