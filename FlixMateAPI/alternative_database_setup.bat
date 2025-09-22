@echo off
echo ============================================================================
echo FlixMate Database Connection Fix
echo ============================================================================
echo.

echo The issue you're experiencing is due to SQL Server user authentication.
echo Here are your options to fix this:
echo.

echo ============================================================================
echo OPTION 1: Use Windows Authentication (Recommended)
echo ============================================================================
echo.
echo This is the easiest solution. Update your application.properties to use
echo Windows Authentication instead of SQL Server Authentication.
echo.

echo ============================================================================
echo OPTION 2: Fix the SQL Server User
echo ============================================================================
echo.
echo 1. Open SQL Server Management Studio as Administrator
echo 2. Connect using Windows Authentication
echo 3. Run the fix_database_connection.sql script
echo.

echo ============================================================================
echo OPTION 3: Use Different Database Credentials
echo ============================================================================
echo.
echo You can use 'sa' account or create a new user with proper permissions.
echo.

set /p choice="Which option would you like to use? (1/2/3): "

if "%choice%"=="1" goto windows_auth
if "%choice%"=="2" goto sql_server_fix
if "%choice%"=="3" goto different_creds

echo Invalid choice. Please run the script again.
pause
exit /b 1

:windows_auth
echo.
echo Updating application.properties for Windows Authentication...
echo.

REM Backup the current file
copy "src\main\resources\application.properties" "src\main\resources\application.properties.backup" >nul 2>&1

REM Create new application.properties with Windows Authentication
(
echo # Primary Database Configuration ^(SQL Server - Windows Authentication^)
echo spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=FLIXMATE_2_0;encrypt=true;trustServerCertificate=true;integratedSecurity=true
echo spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
echo.
echo # JPA/Hibernate Configuration
echo spring.jpa.hibernate.ddl-auto=update
echo spring.jpa.show-sql=true
echo spring.jpa.open-in-view=false
echo spring.jpa.properties.hibernate.jdbc.time_zone=UTC
echo spring.jpa.properties.hibernate.format_sql=true
echo spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
echo.
echo # Ensure proper transaction handling for data persistence
echo spring.jpa.properties.hibernate.connection.autocommit=false
echo spring.jpa.properties.hibernate.jdbc.batch_size=20
echo spring.jpa.properties.hibernate.order_inserts=true
echo spring.jpa.properties.hibernate.order_updates=true
echo spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
echo.
echo # Connection pool settings for reliability
echo spring.datasource.hikari.connection-timeout=20000
echo spring.datasource.hikari.maximum-pool-size=10
echo spring.datasource.hikari.minimum-idle=5
echo spring.datasource.hikari.idle-timeout=300000
echo spring.datasource.hikari.max-lifetime=1200000
echo spring.datasource.hikari.auto-commit=false
echo.
echo # Email Configuration
echo spring.mail.host=smtp.gmail.com
echo spring.mail.port=587
echo spring.mail.username=your-email@gmail.com
echo spring.mail.password=your-app-password
echo spring.mail.properties.mail.smtp.auth=true
echo spring.mail.properties.mail.smtp.starttls.enable=true
echo.
echo # Actuator Configuration
echo management.endpoints.web.exposure.include=health,metrics,info
echo management.endpoint.health.show-details=always
) > "src\main\resources\application.properties"

echo.
echo SUCCESS: Updated application.properties for Windows Authentication!
echo.
echo Make sure you:
echo 1. Have created the FLIXMATE_2_0 database
echo 2. Run the enhanced schema script in SSMS
echo 3. Your Windows user has access to SQL Server
echo.
goto test_connection

:sql_server_fix
echo.
echo Please follow these steps:
echo.
echo 1. Open SQL Server Management Studio as Administrator
echo 2. Connect using Windows Authentication
echo 3. Open and execute the file: fix_database_connection.sql
echo 4. After running the script, come back and press any key
echo.
pause
goto test_connection

:different_creds
echo.
set /p new_user="Enter SQL Server username (or 'sa'): "
set /p new_password="Enter password: "

echo.
echo Updating application.properties with new credentials...

REM Backup the current file
copy "src\main\resources\application.properties" "src\main\resources\application.properties.backup" >nul 2>&1

REM Create new application.properties with new credentials
(
echo # Primary Database Configuration ^(SQL Server^)
echo spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=FLIXMATE_2_0;encrypt=true;trustServerCertificate=true
echo spring.datasource.username=%new_user%
echo spring.datasource.password=%new_password%
echo spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
echo.
echo # JPA/Hibernate Configuration
echo spring.jpa.hibernate.ddl-auto=update
echo spring.jpa.show-sql=true
echo spring.jpa.open-in-view=false
echo spring.jpa.properties.hibernate.jdbc.time_zone=UTC
echo spring.jpa.properties.hibernate.format_sql=true
echo spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
echo.
echo # Ensure proper transaction handling for data persistence
echo spring.jpa.properties.hibernate.connection.autocommit=false
echo spring.jpa.properties.hibernate.jdbc.batch_size=20
echo spring.jpa.properties.hibernate.order_inserts=true
echo spring.jpa.properties.hibernate.order_updates=true
echo spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
echo.
echo # Connection pool settings for reliability
echo spring.datasource.hikari.connection-timeout=20000
echo spring.datasource.hikari.maximum-pool-size=10
echo spring.datasource.hikari.minimum-idle=5
echo spring.datasource.hikari.idle-timeout=300000
echo spring.datasource.hikari.max-lifetime=1200000
echo spring.datasource.hikari.auto-commit=false
echo.
echo # Email Configuration
echo spring.mail.host=smtp.gmail.com
echo spring.mail.port=587
echo spring.mail.username=your-email@gmail.com
echo spring.mail.password=your-app-password
echo spring.mail.properties.mail.smtp.auth=true
echo spring.mail.properties.mail.smtp.starttls.enable=true
echo.
echo # Actuator Configuration
echo management.endpoints.web.exposure.include=health,metrics,info
echo management.endpoint.health.show-details=always
) > "src\main\resources\application.properties"

echo.
echo SUCCESS: Updated application.properties with new credentials!
goto test_connection

:test_connection
echo.
echo ============================================================================
echo Ready to Test!
echo ============================================================================
echo.
echo Now you can:
echo 1. Make sure the database and schema are created
echo 2. Try running your Spring Boot application again
echo.
echo The sample movies you saw are part of the test data and include:
echo - The Avengers ^(2012^)
echo - Inception ^(2010^)
echo - The Dark Knight ^(2008^)
echo - Interstellar ^(2014^)
echo - Parasite ^(2019^)
echo.
echo These are automatically created for testing purposes.
echo.
echo Press any key to exit...
pause >nul
exit /b 0
