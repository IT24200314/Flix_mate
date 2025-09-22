# FlixMate Database Fresh Setup Guide

## 🚨 **Issue:** Still Seeing Only 5 Old Movies

You're seeing only 5 movies because the old database tables and data are still present, conflicting with the new comprehensive setup.

## ✅ **Solution:** Complete Database Recreation

Follow these steps to completely clean and recreate your database with 20 new movies:

### **Step 1: Clean & Recreate Database**
```bash
# This will DELETE the old database and create a fresh one
clean_and_recreate_database.bat
```

**What this does:**
- 🗑️ **Drops** the entire `FLIXMATE_UPDATED` database
- 🆕 **Creates** a fresh `FLIXMATE_UPDATED` database  
- 📊 **Executes** the new comprehensive SQL setup
- ✅ **Verifies** data insertion (20 movies, users, showtimes, etc.)

### **Step 2: Verify Database Setup**
```bash
# Check if all data was inserted correctly
verify_database_setup.bat
```

**Expected Output:**
```
Movies: 20 records
Users: 10 records  
Cinema Halls: 6 records
Showtimes: 140+ records
Seats: 200+ records
Reviews: 7 records
```

### **Step 3: Start Application**
```bash
# Start with Java auto-detection
check_java_and_run.bat
```

### **Step 4: Test in Browser**
1. **Navigate to:** `http://localhost:8080/movies.html`
2. **Expected:** See **20 movies** with poster images
3. **Verify:** Movies include recent films like:
   - Avengers: Endgame
   - Spider-Man: No Way Home  
   - The Batman
   - Top Gun: Maverick
   - Barbie
   - Oppenheimer
   - And 14 more...

## 🎯 **What's Different in the New Setup**

### **Old Database (5 movies):**
- ❌ Limited sample data
- ❌ Missing poster URLs
- ❌ Outdated movie information
- ❌ No proper relationships

### **New Database (20 movies):**
- ✅ **20 comprehensive movies** with full metadata
- ✅ **Poster images** properly linked
- ✅ **Recent blockbusters** (2019-2023)
- ✅ **Complete ecosystem:** users, showtimes, bookings, reviews
- ✅ **Dynamic showtimes** (next 14 days)
- ✅ **Performance indexes** for faster queries

## 🔧 **Script Details**

### **clean_and_recreate_database.bat**
- Safely drops existing database
- Creates fresh database with proper collation
- Executes the comprehensive SQL setup
- Verifies all data insertion
- Provides detailed error reporting

### **verify_database_setup.bat**  
- Checks database connectivity
- Shows record counts for all tables
- Lists all 20 movies with details
- Displays sample users and showtimes
- Confirms setup success

## 🚨 **Important Notes**

1. **⚠️ Data Loss Warning:** The cleanup script will DELETE all existing data
2. **🔐 Backup:** If you have important custom data, back it up first
3. **🔗 Credentials:** Scripts use the same database credentials from application.properties
4. **⏰ Time:** The process takes 2-3 minutes to complete

## 📋 **Troubleshooting**

### **If You Still See Old Data:**
1. **Clear browser cache** (Ctrl+Shift+Delete)
2. **Restart Spring Boot application** completely
3. **Check console logs** for any database connection errors
4. **Re-run verification script** to confirm 20 movies exist

### **If Connection Fails:**
1. **Verify SQL Server is running**
2. **Check credentials** in application.properties
3. **Ensure user has** CREATE/DROP database permissions
4. **Try connecting manually** with SQL Server Management Studio

## 🎉 **Success Indicators**

After running the cleanup and setup:
- ✅ **Verification script** shows 20 movies
- ✅ **Movies page** displays all 20 movies with images
- ✅ **Database queries** return comprehensive data
- ✅ **No console errors** in Spring Boot logs

**Result:** You'll have a completely fresh, comprehensive FlixMate database with 20 movies and full sample data!
