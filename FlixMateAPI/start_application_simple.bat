@echo off
echo Starting FlixMate API with SQL Server Database...

REM Set Java environment
set JAVA_HOME=C:\Users\isiri\.jdks\openjdk-24.0.2+12-54
set PATH=%JAVA_HOME%\bin;%PATH%

REM Set Spring profile to use SQL Server database
set SPRING_PROFILES_ACTIVE=default

REM Start the application using Maven
echo Starting application...
mvn spring-boot:run -Dspring-boot.run.profiles=default

echo.
echo Application started successfully!
echo.
echo Access the application at: http://localhost:8080
echo.
echo Default credentials:
echo - Admin: admin@example.com / password123
echo - User: user@example.com / userpass123
echo.
echo Press any key to stop the application...
pause > nul
