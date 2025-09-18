# FlixMate Database Setup and Error Handling Guide

## Overview
This guide explains how to set up the FlixMate database using the enhanced SQL file and how the improved error handling works.

## Database Setup

### 1. Enhanced SQL File
The main SQL file is located at: `src/main/resources/sql/FlixMate_Complete_Database_Enhanced.sql`

This file includes:
- **Comprehensive error handling** with TRY-CATCH blocks
- **Data validation** with CHECK constraints
- **Better indexing** for performance
- **Enhanced logging** for debugging
- **Sample data** with error handling

### 2. Key Features of the Enhanced SQL File

#### Error Handling
- All table creation operations are wrapped in TRY-CATCH blocks
- Detailed error messages for debugging
- Graceful handling of existing objects
- Transaction safety with XACT_ABORT

#### Data Validation
- CHECK constraints for data integrity
- Foreign key constraints for referential integrity
- Unique constraints to prevent duplicates
- Range validation for numeric fields

#### Performance Optimizations
- Strategic indexes on frequently queried columns
- Composite indexes for complex queries
- Proper foreign key relationships

### 3. Running the Database Setup

1. **Connect to SQL Server Management Studio**
2. **Open the enhanced SQL file**: `FlixMate_Complete_Database_Enhanced.sql`
3. **Execute the script** - it will:
   - Create the database if it doesn't exist
   - Create all tables with proper constraints
   - Insert sample data
   - Create performance indexes
   - Verify the setup

### 4. Database Schema

#### Core Tables
- **user_status**: User roles and permissions
- **users**: User accounts and authentication
- **movies**: Movie information and metadata
- **cinema_halls**: Cinema hall configurations
- **seats**: Seat information and availability
- **showtimes**: Movie showtime schedules
- **bookings**: User booking records
- **booking_seats**: Many-to-many relationship for bookings and seats

#### Supporting Tables
- **loyalty_points**: User loyalty point system
- **discount_codes**: Discount and promotion codes
- **payments**: Payment transaction records
- **reviews**: Movie reviews and ratings
- **reports**: System reports and analytics
- **staff_schedules**: Staff scheduling information

## Error Handling Improvements

### 1. Service Layer Enhancements

#### BookingService
- **Input validation** for all parameters
- **Detailed error logging** for debugging
- **Graceful error handling** with meaningful messages
- **Transaction safety** for booking operations

#### MovieService
- **Data validation** for movie information
- **Comprehensive error handling** for all operations
- **Detailed logging** for troubleshooting

### 2. Global Exception Handler

A new `GlobalExceptionHandler` class provides:
- **Centralized error handling** for all controllers
- **Consistent error response format**
- **Proper HTTP status codes**
- **Detailed error logging**

### 3. Error Response Format

All errors now return a consistent JSON format:
```json
{
  "timestamp": "2025-09-18T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/endpoint"
}
```

## Troubleshooting

### Common Issues and Solutions

#### 1. Database Connection Issues
- **Check SQL Server is running**
- **Verify connection string in application.properties**
- **Ensure database exists and is accessible**

#### 2. Booking Issues
- **Check if showtime exists**
- **Verify seat availability**
- **Ensure user is authenticated**
- **Check foreign key constraints**

#### 3. Movie Management Issues
- **Verify user has admin privileges**
- **Check data validation constraints**
- **Ensure proper JSON format in requests**

### Debugging Steps

1. **Check application logs** for detailed error messages
2. **Verify database connectivity** using SQL Server Management Studio
3. **Test individual endpoints** using Postman or similar tools
4. **Check data integrity** in the database
5. **Review error response format** for clues

## API Endpoints

### Movie Management
- `GET /api/movies` - Get all movies
- `GET /api/movies/{id}` - Get movie by ID
- `POST /api/movies` - Add new movie (Admin only)
- `PUT /api/movies/{id}` - Update movie (Admin only)
- `DELETE /api/movies/{id}` - Delete movie (Admin only)

### Booking Management
- `GET /api/bookings/user` - Get user's bookings
- `POST /api/bookings/{showtimeId}` - Create new booking
- `GET /api/bookings/available/{showtimeId}` - Get available seats
- `GET /api/bookings/{id}` - Get booking by ID
- `PUT /api/bookings/{id}` - Update booking
- `DELETE /api/bookings/{id}` - Cancel booking

## Sample Data

The enhanced SQL file includes sample data for:
- 5 movies with different genres
- 5 cinema halls with varying capacities
- 750+ seats across all halls
- 12 showtimes with different pricing
- 2 sample users (admin and regular user)
- Sample loyalty points and discount codes

## Performance Considerations

### Indexes Created
- `IDX_ShowTime_StartTime` - For showtime queries
- `IDX_Booking_Status` - For booking status queries
- `IDX_User_Email` - For user authentication
- `IDX_Movie_Title` - For movie searches
- `IDX_Seat_Hall_Status` - For seat availability queries
- `IDX_Booking_User` - For user booking queries
- `IDX_ShowTime_Movie` - For movie showtime queries

### Best Practices
- Use parameterized queries to prevent SQL injection
- Implement proper pagination for large result sets
- Monitor database performance and query execution times
- Regular database maintenance and index optimization

## Security Considerations

- All user inputs are validated
- SQL injection prevention through parameterized queries
- Proper authentication and authorization checks
- Sensitive data is not logged in error messages
- Database credentials are stored securely

## Maintenance

### Regular Tasks
1. **Monitor error logs** for recurring issues
2. **Check database performance** and optimize queries
3. **Update sample data** as needed
4. **Review and update error handling** based on new requirements
5. **Backup database** regularly

### Monitoring
- Application logs for error patterns
- Database performance metrics
- API response times
- Error rate monitoring

## Support

For issues or questions:
1. Check the application logs first
2. Review this documentation
3. Test with sample data
4. Verify database connectivity
5. Check API endpoint responses

The enhanced error handling and comprehensive SQL setup should resolve most common issues with the FlixMate booking and movie management features.
