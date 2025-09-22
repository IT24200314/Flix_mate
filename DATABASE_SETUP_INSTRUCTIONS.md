# FlixMate Enhanced Database Setup Instructions

## Overview
This guide will help you set up the enhanced FlixMate database schema with all the latest improvements and optimizations.

## Prerequisites
- SQL Server installed and running
- SQL Server Management Studio (SSMS) or any SQL client
- Access to create databases on your SQL Server instance

## Quick Setup Steps

### Step 1: Open SQL Server Management Studio
1. Launch SQL Server Management Studio (SSMS)
2. Connect to your SQL Server instance

### Step 2: Create the Database
Execute this command first:
```sql
CREATE DATABASE FLIXMATE_2_0;
```

### Step 3: Execute the Enhanced Schema
1. Open the file: `src/main/resources/sql/flixmate_enhanced_schema.sql`
2. Copy the entire content
3. Paste and execute it in SSMS (make sure you're connected to the FLIXMATE_2_0 database)

### Step 4: Verify Setup
Run the verification script to ensure everything is set up correctly:
```bash
.\verify_enhanced_database.bat
```

## What's Included in the Enhanced Schema

### 📊 **14 Optimized Tables**
- `user_status` - User roles and status management
- `users` - User accounts and profiles
- `movies` - Movie catalog with metadata
- `cinema_halls` - Theater halls configuration
- `seats` - Seat arrangements and types
- `showtimes` - Movie scheduling
- `bookings` - Booking transactions
- `booking_seats` - Seat reservations
- `payments` - Payment processing
- `reviews` - Movie reviews and ratings
- `loyalty_points` - Customer loyalty system
- `discount_codes` - Promotional discounts
- `promotional_banners` - Marketing banners
- `reports` - System reporting
- `staff_schedules` - Staff management

### 🚀 **Performance Features**
- **20+ Indexes** for optimized queries
- **2 Views** for common data retrieval
- **2 Stored Procedures** for business logic
- **3 Triggers** for automated data management

### 📝 **Default Data Included**
- 5 User statuses (Active, Admin, Suspended, Inactive, Moderator)
- 1 Admin user account
- Clean database ready for content creation

## Default Admin Account
After setup, you can log in with:
- **Username:** SE
- **Password:** @Emuib0326
- **Email:** admin@flixmate.com

## Database Configuration
The application is configured to connect to:
- **Database:** FLIXMATE_2_0
- **Server:** localhost:1433
- **Authentication:** SQL Server Authentication
- **Username:** SE
- **Password:** @Emuib0326

Update `src/main/resources/application.properties` if you need different connection settings.

## Verification
After setup, you should see:
- All 14 tables created and ready for use
- Admin user ready for login
- Clean database ready for content creation
- All indexes and constraints in place

## Troubleshooting

### Common Issues:
1. **Database already exists**: Drop the existing database first or use a different name
2. **Permission errors**: Ensure your SQL Server user has CREATE DATABASE privileges
3. **Connection issues**: Verify SQL Server is running and accessible

### Alternative Setup Methods:
1. **Using sqlcmd**: Run `setup_enhanced_database.bat` and choose automated setup
2. **Manual execution**: Copy-paste the SQL content directly in SSMS
3. **Custom script**: Modify the SQL file for your specific requirements

## Next Steps
1. Start the Spring Boot application
2. Test the admin login
3. Verify all endpoints are working
4. Add your own movies and showtimes
5. Test the booking flow

## Support
If you encounter any issues:
1. Check the SQL Server logs
2. Verify all prerequisites are met
3. Ensure the SQL file executed completely
4. Run the verification script to identify missing components
