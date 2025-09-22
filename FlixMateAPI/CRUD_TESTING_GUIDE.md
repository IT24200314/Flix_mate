# FlixMate CRUD Testing Guide

## Overview
This guide provides comprehensive testing for all 6 functions of the FlixMate system with complete CRUD operations, error handling, and data integrity verification.

## Functions Tested

### 1. Manage Movie Listings and Showtimes
- **Create**: POST `/api/movies` - Add new movies with validation
- **Read**: GET `/api/movies` - List all movies, GET `/api/movies/{id}` - Get specific movie
- **Update**: PUT `/api/movies/{id}` - Update movie details
- **Delete**: DELETE `/api/movies/{id}` - Remove movies (with cascade handling)

### 2. Analyze Performance, Revenue, and Trends (Generate Reports)
- **Create**: POST `/api/reports` - Generate reports (REVENUE, POPULARITY, TICKET_SALES)
- **Read**: GET `/api/reports` - List all reports, GET `/api/reports/{type}` - Get specific report types
- **Delete**: DELETE `/api/reports/{id}` - Remove reports

### 3. Secure Payment Processing
- **Create**: POST `/api/payments` - Process payments with booking validation
- **Read**: GET `/api/payments/logs` - Get payment logs, GET `/api/payments/{id}` - Get specific payment
- **Update**: PUT `/api/payments/{id}` - Update payment status (e.g., REFUNDED)
- **Delete**: DELETE `/api/payments/{id}` - Remove payments (for audit purposes)

### 4. Book Tickets (Seat Selection & Checkout)
- **Create**: POST `/api/bookings/{showtimeId}` - Create bookings with seat selection
- **Read**: GET `/api/bookings/user` - Get user bookings, GET `/api/bookings/available/{showtimeId}` - Get available seats
- **Update**: PUT `/api/bookings/{id}` - Update booking (change seats)
- **Delete**: DELETE `/api/bookings/{id}` - Cancel bookings (release seats)

### 5. Browse Movies, Showtimes, and Locations
- **Read**: GET `/api/movies` with filters (title, genre, year) - Browse and search movies

### 6. Rate and Review Movies
- **Create**: POST `/api/movies/{movieId}/reviews` - Add reviews with rating validation
- **Read**: GET `/api/movies/{movieId}/reviews` - Get movie reviews
- **Update**: PUT `/api/movies/{movieId}/reviews/{reviewId}` - Update user's own reviews
- **Delete**: DELETE `/api/movies/{movieId}/reviews/{reviewId}` - Delete user's own reviews

## Prerequisites

### Required Software
1. **Java JDK 17 or higher**
   - Download from: https://adoptium.net/
   - Add to system PATH

2. **Maven** (included as wrapper)
   - Uses `mvnw.cmd` wrapper script

3. **Database**
   - SQL Server (for production)
   - H2 Database (for testing - included)

### Database Setup
The test suite uses H2 in-memory database for testing, which is automatically configured. For production testing with SQL Server:

1. Ensure SQL Server is running on localhost:1433
2. Update `application.properties` with correct database credentials
3. Run the schema creation script: `src/main/resources/sql/corrected_schema.sql`

## Running the Tests

### Option 1: Automated Test Suite (Recommended)
```bash
# Run the comprehensive test suite
run_crud_tests.bat
```

This script will:
- Check Java installation
- Run all CRUD tests
- Display results
- Generate test reports

### Option 2: Manual Maven Execution
```bash
# Run all tests
mvnw.cmd test

# Run specific test class
mvnw.cmd test -Dtest=FlixMateCrudIntegrationTest

# Run with test profile
mvnw.cmd test -Dspring.profiles.active=test
```

### Option 3: IDE Execution
1. Open the project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Navigate to `src/test/java/com/flixmate/flixmate/api/FlixMateCrudIntegrationTest.java`
3. Run the test class or individual test methods

## Test Structure

### Test Categories
1. **CRUD Operations**: Complete Create, Read, Update, Delete testing
2. **Error Handling**: 404, 401, 400 error responses
3. **Data Validation**: Input validation and business rules
4. **Authentication**: Protected endpoint access
5. **Authorization**: Role-based access control
6. **Data Integrity**: Database consistency verification

### Test Data Setup
Each test method:
- Cleans existing data
- Creates fresh test data
- Executes operations
- Verifies results
- Cleans up after execution

## Test Results

### Console Output
The test execution provides:
- Real-time test progress
- Pass/fail status for each operation
- Error details for failures
- Summary statistics

### Generated Reports
1. **HTML Report**: `flixmate_crud_test_report.html`
   - Comprehensive visual report
   - Function-by-function breakdown
   - Endpoint coverage details
   - Test execution statistics

2. **JSON Summary**: `test_summary.json`
   - Machine-readable test results
   - Detailed metrics
   - Function coverage data

3. **Maven Surefire Reports**: `target/surefire-reports/`
   - Standard Maven test reports
   - XML format for CI/CD integration

## Troubleshooting

### Common Issues

#### Java Not Found
```
ERROR: Java is not installed or not in PATH
```
**Solution**: Install Java JDK 17+ and add to PATH

#### Maven Wrapper Issues
```
ERROR: Maven wrapper not found
```
**Solution**: Ensure you're in the project root directory

#### Database Connection Issues
```
Connection refused or timeout
```
**Solution**: 
- For testing: H2 database should work automatically
- For production: Check SQL Server connection settings

#### Test Failures
```
Some tests failed
```
**Solution**:
1. Check test output for specific error messages
2. Verify database schema is correct
3. Ensure all dependencies are installed
4. Check application configuration

### Debug Mode
Run tests with debug output:
```bash
mvnw.cmd test -Dtest=FlixMateCrudIntegrationTest -X
```

## Test Coverage

### Functions Covered: 6/6 (100%)
- ✅ Movie Listings and Showtimes
- ✅ Performance, Revenue, and Trends
- ✅ Secure Payment Processing
- ✅ Book Tickets (Seat Selection & Checkout)
- ✅ Browse Movies, Showtimes, and Locations
- ✅ Rate and Review Movies

### CRUD Operations Covered: 4/4 (100%)
- ✅ Create (POST)
- ✅ Read (GET)
- ✅ Update (PUT)
- ✅ Delete (DELETE)

### Endpoints Tested: 25+
- All major API endpoints
- Error handling scenarios
- Authentication/authorization
- Data validation

## Continuous Integration

### GitHub Actions (Example)
```yaml
name: FlixMate CRUD Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: windows-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
    - name: Run CRUD Tests
      run: run_crud_tests.bat
    - name: Generate Report
      run: python generate_test_report.py
    - name: Upload Reports
      uses: actions/upload-artifact@v2
      with:
        name: test-reports
        path: |
          flixmate_crud_test_report.html
          test_summary.json
          target/surefire-reports/
```

## Maintenance

### Adding New Tests
1. Add test methods to `FlixMateCrudIntegrationTest.java`
2. Follow naming convention: `test[Function]Crud_[Operation]`
3. Include proper setup and cleanup
4. Add to test report generation if needed

### Updating Test Data
Modify the `setupTestData()` method in the test class to include new test scenarios.

### Extending Coverage
Add new test classes for:
- Performance testing
- Load testing
- Security testing
- Integration testing with external services

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review test output and error messages
3. Verify all prerequisites are met
4. Check database connectivity and schema

## Conclusion

This comprehensive testing suite ensures that all FlixMate functions work correctly with complete CRUD operations, proper error handling, and data integrity. The automated testing process provides confidence in the system's reliability and maintainability.
