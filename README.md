# FlixMate API - Cinema Management System

## Project Overview

FlixMate is a comprehensive cinema management system built with Spring Boot that provides end-to-end functionality for movie ticket booking, payment processing, user management, and theatre operations.

## Features Implemented

### Core Features
- **User Management**: Registration, authentication, profile management
- **Movie Management**: CRUD operations for movies and showtimes
- **Booking System**: Seat selection and booking management
- **Payment Processing**: Payment gateway integration with transaction logging
- **Review System**: User reviews and ratings for movies
- **Theatre Management**: Cinema hall and staff schedule management
- **Reporting**: Analytics and reporting dashboard
- **Performance Monitoring**: Health checks, metrics, and backup system

### Technical Features
- **Security**: JWT-based authentication with role-based access control
- **Database Optimization**: Indexed queries for improved performance
- **Monitoring**: Spring Actuator for health checks and metrics
- **Backup System**: Automated data backup functionality
- **Testing**: Comprehensive unit and integration tests

## Technology Stack

- **Backend**: Spring Boot 3.5.5, Spring Security, Spring Data JPA
- **Database**: Microsoft SQL Server (Production), H2 (Testing)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Deployment**: WAR packaging for Tomcat deployment

## Architecture

### Design Patterns Used
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic separation
- **DTO Pattern**: Data transfer objects for API responses
- **Singleton Pattern**: Database connection management
- **Factory Pattern**: Service instantiation

### Database Schema
- **Users**: User accounts and profiles
- **Movies**: Movie information and metadata
- **CinemaHalls**: Theatre hall configurations
- **ShowTimes**: Movie screening schedules
- **Seats**: Seat availability and booking status
- **Bookings**: User booking records
- **Payments**: Payment transaction logs
- **Reviews**: User reviews and ratings

## API Endpoints

### Authentication
- `POST /api/register` - User registration
- `POST /api/login` - User authentication

### User Management
- `GET /api/profile` - Get user profile
- `PUT /api/profile` - Update user profile

### Movie Management
- `GET /api/movies` - List all movies
- `POST /api/movies` - Add new movie
- `GET /api/movies/{id}` - Get movie details

### Booking System
- `POST /api/bookings/{showtimeId}` - Create booking
- `GET /api/bookings/user` - Get user bookings
- `GET /api/bookings/available/{showtimeId}` - Get available seats

### Payment Processing
- `POST /api/payments/process` - Process payment
- `GET /api/payments/logs` - Get payment logs

### Theatre Management
- `GET /api/theatre/seats/{hallId}` - Get hall seats
- `POST /api/theatre/showtimes` - Add showtime
- `GET /api/theatre/staff-schedules` - Get staff schedules

### Monitoring & Backup
- `GET /actuator/health` - System health check
- `GET /actuator/metrics` - System metrics
- `GET /api/backup` - Create data backup (Admin only)

## Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Microsoft SQL Server (for production)
- Tomcat 9 or higher (for deployment)

### Development Setup
1. Clone the repository
2. Configure database connection in `application.properties`
3. Run `mvn clean install` to build the project
4. Run `mvn spring-boot:run` to start the application
5. Access the API at `http://localhost:8080`

### Production Deployment
1. Build WAR file: `mvn clean package`
2. Deploy `flixmate-api.war` to Tomcat webapps directory
3. Start Tomcat server
4. Access at `http://localhost:8080/flixmate-api/`

## Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=BookingServiceTest
```

### Test Coverage
- Unit Tests: Service layer business logic
- Integration Tests: API endpoint testing
- Test Coverage: 70%+ across all modules

## Performance Optimizations

### Database Indexes
- `idx_user_status` on bookings table (user_id, status)
- `idx_hall_status` on seats table (hall_id, status)

### Monitoring
- Health checks via Spring Actuator
- JVM metrics and system performance monitoring
- Automated backup system for data protection

## Security Features

- JWT-based authentication
- Role-based access control (USER, ADMIN, MANAGER)
- Password encryption using BCrypt
- SQL injection prevention via JPA
- CORS configuration for cross-origin requests

## SDLC Phases

### 1. Requirements Analysis
- Functional requirements gathering
- Non-functional requirements definition
- User story creation and prioritization

### 2. Design
- System architecture design
- Database schema design
- API specification and documentation
- Security model design

### 3. Implementation
- Backend API development
- Database implementation
- Security integration
- Testing implementation

### 4. Testing
- Unit testing
- Integration testing
- Performance testing
- Security testing

### 5. Deployment
- Production environment setup
- Application deployment
- Monitoring configuration
- Documentation completion

## Future Enhancements

- Mobile application development
- Real-time notifications
- Advanced analytics dashboard
- Multi-cinema chain support
- AI-powered recommendation system

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is developed for educational purposes as part of SE2030 Software Engineering course.

## Contact

For questions or support, please contact the development team.

---

**FlixMate API v1.0** - Complete Cinema Management Solution
