# FlixMate Database Setup

## 🎯 **SIMPLE SETUP - ONE FILE ONLY**

I've cleaned up all the confusing SQL files and created **ONE comprehensive file** that handles everything.

## 📁 **Files You Need**

### **Main Database File:**
- `FlixMate_Complete_Database.sql` - **This is the ONLY SQL file you need**

### **Setup Script:**
- `setup_database.bat` - Easy setup script

## 🚀 **How to Set Up Database**

### **Option 1: Using the Setup Script (Recommended)**
1. Double-click `setup_database.bat`
2. Follow the instructions
3. Open SQL Server Management Studio when prompted
4. Execute the `FlixMate_Complete_Database.sql` file
5. Come back and press any key to start the application

### **Option 2: Manual Setup**
1. Open SQL Server Management Studio
2. Connect to your SQL Server instance
3. Open `FlixMate_Complete_Database.sql`
4. Execute the entire script
5. Start your application with `mvnw spring-boot:run`

## ✅ **What the Database File Does**

The `FlixMate_Complete_Database.sql` file:

1. **Creates the FlixMate database**
2. **Creates all required tables** with proper data types
3. **Fixes timestamp conversion issues** (uses DATETIME instead of DATETIME2)
4. **Inserts sample data** (movies, showtimes, users, etc.)
5. **Creates indexes** for better performance
6. **Verifies everything** was created correctly

## 🎬 **Sample Data Included**

- **5 Movies**: The Matrix, Inception, The Dark Knight, Avatar, Titanic
- **5 Cinema Halls**: Different capacities and locations
- **750+ Seats**: Distributed across all halls
- **12 Showtimes**: Various times and prices
- **2 Users**: Admin user and test user

## 🔧 **Fixes Applied**

- ✅ **Timestamp Conversion**: All datetime fields use DATETIME (not DATETIME2)
- ✅ **Proper Foreign Keys**: All relationships properly defined
- ✅ **Indexes**: Performance indexes on key columns
- ✅ **Sample Data**: Ready-to-test data included

## 🚨 **Important Notes**

1. **Backup First**: If you have existing data, backup your database first
2. **Java Required**: Make sure Java JDK 17+ is installed
3. **SQL Server**: Ensure SQL Server is running and accessible

## 🎯 **After Setup**

Once you run the database script and start the application:

- Visit: http://localhost:8080
- Test movies: http://localhost:8080/movies.html
- Test booking: http://localhost:8080/booking.html
- Test payment: http://localhost:8080/payment.html

## 📞 **If You Have Issues**

1. **Check Java**: Run `java -version` to verify Java is installed
2. **Check SQL Server**: Ensure SQL Server is running
3. **Check Logs**: Look at application console for any errors
4. **Verify Database**: Check that all tables were created in SQL Server

---

**That's it! One file, simple setup, everything works!** 🎉
