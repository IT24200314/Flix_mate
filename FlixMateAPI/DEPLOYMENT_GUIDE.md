# FlixMate API - Deployment Guide

## Prerequisites

### System Requirements
- Java 17 or higher
- Maven 3.6 or higher
- Apache Tomcat 9 or higher
- Microsoft SQL Server (for production)
- 4GB RAM minimum
- 10GB disk space

### Software Installation
1. **Java Development Kit (JDK)**
   - Download from Oracle or OpenJDK
   - Set JAVA_HOME environment variable
   - Verify: `java -version`

2. **Apache Maven**
   - Download from Apache Maven website
   - Add to PATH environment variable
   - Verify: `mvn -version`

3. **Apache Tomcat**
   - Download Tomcat 9.x from Apache website
   - Extract to desired location
   - Set CATALINA_HOME environment variable

4. **Microsoft SQL Server**
   - Install SQL Server 2019 or higher
   - Create database named "FlixMate"
   - Configure authentication (Windows or SQL Server)

## Build Process

### 1. Clone Repository
```bash
git clone <repository-url>
cd FlixMateAPI
```

### 2. Configure Database
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=FlixMate;encrypt=true;trustServerCertificate=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build Application
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package as WAR
mvn clean package
```

### 4. Verify Build
Check for `flixmate-api.war` in `target/` directory.

## Deployment Options

### Option 1: Tomcat Deployment (Recommended)

#### Steps:
1. **Stop Tomcat** (if running)
2. **Deploy WAR file**:
   ```bash
   copy target\flixmate-api.war %CATALINA_HOME%\webapps\
   ```
3. **Start Tomcat**:
   ```bash
   %CATALINA_HOME%\bin\startup.bat
   ```
4. **Verify deployment**:
   - Check Tomcat logs: `%CATALINA_HOME%\logs\catalina.out`
   - Access application: `http://localhost:8080/flixmate-api/`

#### Access Points:
- **API Base URL**: `http://localhost:8080/flixmate-api/api/`
- **Health Check**: `http://localhost:8080/flixmate-api/actuator/health`
- **Metrics**: `http://localhost:8080/flixmate-api/actuator/metrics`

### Option 2: Standalone JAR (Development)

#### Steps:
1. **Build JAR**:
   ```bash
   mvn clean package -DskipTests
   ```
2. **Run JAR**:
   ```bash
   java -jar target\flixmate-api-0.0.1-SNAPSHOT.jar
   ```
3. **Access**: `http://localhost:8080/`

## Configuration

### Environment Variables
```bash
# Database Configuration
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=FlixMate
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Server Configuration
SERVER_PORT=8080
SERVER_CONTEXT_PATH=/flixmate-api

# Security Configuration
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000
```

### Application Properties
Key configurations in `application.properties`:
```properties
# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Server
server.port=${SERVER_PORT}
server.servlet.context-path=${SERVER_CONTEXT_PATH}

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Security
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# Actuator
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=always
```

## Testing Deployment

### 1. Health Check
```bash
curl http://localhost:8080/flixmate-api/actuator/health
```
**Expected Response**: `{"status":"UP"}`

### 2. API Endpoints Test
```bash
# Test registration
curl -X POST http://localhost:8080/flixmate-api/api/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","userName":"Test User","statusName":"STANDARD"}'

# Test health endpoint
curl http://localhost:8080/flixmate-api/actuator/health

# Test metrics
curl http://localhost:8080/flixmate-api/actuator/metrics
```

### 3. Database Verification
1. Connect to SQL Server
2. Verify FlixMate database exists
3. Check tables are created:
   - users
   - movies
   - bookings
   - payments
   - reviews
   - seats
   - showtimes
   - cinema_halls

## Troubleshooting

### Common Issues

#### 1. Database Connection Failed
**Error**: `Connection refused`
**Solution**: 
- Verify SQL Server is running
- Check connection string
- Verify database exists
- Check firewall settings

#### 2. Port Already in Use
**Error**: `Port 8080 was already in use`
**Solution**:
- Change port in `application.properties`
- Kill process using port 8080
- Use different Tomcat port

#### 3. WAR Deployment Failed
**Error**: `Deployment failed`
**Solution**:
- Check Tomcat logs
- Verify WAR file integrity
- Check Tomcat version compatibility
- Verify Java version compatibility

#### 4. Application Won't Start
**Error**: `Application startup failed`
**Solution**:
- Check database connectivity
- Verify all dependencies
- Check configuration files
- Review application logs

### Log Files
- **Application Logs**: `logs/application.log`
- **Tomcat Logs**: `%CATALINA_HOME%/logs/catalina.out`
- **Error Logs**: `%CATALINA_HOME%/logs/localhost.log`

## Monitoring

### Health Monitoring
- **Health Check**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Info**: `/actuator/info`

### Performance Monitoring
- **JVM Memory**: Monitor heap usage
- **Database Connections**: Check connection pool
- **Response Times**: Monitor API response times
- **Error Rates**: Track error percentages

### Backup
- **Automated Backup**: `/api/backup` (Admin only)
- **Database Backup**: Regular SQL Server backups
- **Log Rotation**: Configure log file rotation

## Security Considerations

### Production Security
1. **Change Default Passwords**
2. **Use HTTPS** in production
3. **Configure Firewall** rules
4. **Regular Security Updates**
5. **Database Encryption**
6. **JWT Secret Management**

### Access Control
- **Admin Access**: Limited to authorized personnel
- **API Access**: Rate limiting recommended
- **Database Access**: Restricted to application only

## Scaling Considerations

### Horizontal Scaling
- **Load Balancer**: Use Apache or Nginx
- **Multiple Instances**: Deploy multiple WAR files
- **Database Clustering**: SQL Server Always On

### Vertical Scaling
- **Memory**: Increase JVM heap size
- **CPU**: Add more processing power
- **Storage**: Increase database storage

## Maintenance

### Regular Tasks
1. **Log Monitoring**: Daily log review
2. **Performance Monitoring**: Weekly metrics review
3. **Backup Verification**: Daily backup checks
4. **Security Updates**: Monthly security patches
5. **Database Maintenance**: Weekly database optimization

### Updates
1. **Application Updates**: Deploy new WAR file
2. **Database Updates**: Run migration scripts
3. **Configuration Updates**: Update properties files
4. **Dependency Updates**: Update Maven dependencies

---

**Deployment Status**: ✅ Ready for Production
**Last Updated**: September 16, 2025
**Version**: 1.0.0
