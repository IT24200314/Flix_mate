# 🔐 FlixMate Admin Access Guide

## 🎯 **Admin Login Credentials**

### **Primary Admin Account:**
- **Email:** `admin@example.com`
- **Password:** `password123`
- **Role:** ADMIN

### **Alternative Admin Account:**
- **Email:** `admin@flixmate.com`
- **Password:** `password123`
- **Role:** ADMIN

---

## 🚀 **How to Access Admin Panel**

### **Single Login Method**
1. Go to: `http://localhost:8080/login.html`
2. Use admin credentials: `admin@example.com` / `password123`
3. Click "Sign In"
4. You'll be redirected to the movies page with admin controls visible
5. Access admin features:
   - "Add Movie" button in movies page
   - "Admin Panel" link in movies page
   - Edit/Delete options on movie cards
   - Admin link in navigation menu

### **Quick Access**
- Click on the admin credentials in the demo section to auto-fill
- Admin credentials are highlighted in red for easy identification
- Hover over credentials to see they're clickable

---

## 🎬 **Admin Features Available**

### **Movie Management** (`/admin-movie-management.html`)
- ✅ **Add New Movies** - Complete movie details
- ✅ **Update Movie Details** - Edit existing movies
- ✅ **Delete Movies** - Remove movies (with validation)
- ✅ **Archive Movies** - Soft delete option
- ✅ **View All Movies** - Complete movie listing
- ✅ **Manage Showtimes** - Assign showtimes per movie
- ✅ **View Seat Bookings** - Real-time booking analytics
- ✅ **Statistics Dashboard** - System overview

### **Access Control**
- ✅ **Admin-Only Access** - Only ADMIN role can access
- ✅ **Authentication Required** - Must be logged in
- ✅ **Automatic Redirect** - Non-admin users redirected to login
- ✅ **Session Management** - Persistent admin session

---

## 🛡️ **Security Features**

### **Authentication**
- ✅ **Basic Authentication** - Username/password based
- ✅ **Role-Based Access** - ADMIN role required
- ✅ **Session Persistence** - Login state maintained
- ✅ **Automatic Logout** - Redirect on access denied

### **Error Handling**
- ✅ **403 Forbidden** - Access denied for non-admin users
- ✅ **400 Bad Request** - Invalid data or business rule violations
- ✅ **500 Server Error** - Server-side errors
- ✅ **Network Errors** - Connection issues
- ✅ **Validation Errors** - Form validation failures

---

## 🔧 **Error Handling Details**

### **403 Access Denied Errors**
**Causes:**
- User not logged in
- User doesn't have ADMIN role
- Session expired

**Handling:**
- Automatic redirect to login page
- Clear error message
- 2-second delay before redirect

### **400 Bad Request Errors**
**Causes:**
- Invalid movie data
- Missing required fields
- Business rule violations (e.g., deleting movie with showtimes)

**Handling:**
- Specific error messages
- Form validation
- User-friendly feedback

### **500 Server Errors**
**Causes:**
- Database connection issues
- Server-side exceptions
- System errors

**Handling:**
- Generic error message
- Suggestion to try again later
- Logging for debugging

---

## 📋 **Admin Operations**

### **Movie CRUD Operations**
1. **Create Movie**
   - Required: Title, Genre, Duration
   - Optional: Language, Director, Description, Release Year
   - Validation: All fields validated before submission

2. **Update Movie**
   - Same validation as create
   - Preserves existing data
   - Updates all fields

3. **Delete Movie**
   - Checks for active showtimes
   - Prevents deletion if showtimes exist
   - Confirmation dialog required

4. **Archive Movie**
   - Soft delete option
   - Removes from active listings
   - Preserves data for reporting

### **Showtime Management**
1. **View Showtimes** - Per movie
2. **Add Showtime** - Assign to cinema hall
3. **Update Showtime** - Modify times and prices
4. **Delete Showtime** - Remove showtime
5. **View Bookings** - See seat bookings per showtime

### **Analytics & Reports**
1. **Overall Statistics** - Total movies, showtimes, bookings
2. **Per-Movie Statistics** - Individual movie analytics
3. **Seat Booking Analysis** - Real-time availability
4. **Booking Details** - User information and amounts

---

## 🚨 **Troubleshooting**

### **Common Issues**

#### **"Access denied. Admin privileges required."**
**Solution:**
1. Make sure you're logged in with admin credentials
2. Check that the user role is ADMIN
3. Try logging out and logging back in
4. Clear browser cache and cookies

#### **"Failed to save movie"**
**Solution:**
1. Check all required fields are filled
2. Ensure duration is a positive number
3. Verify genre is selected
4. Check for special characters in title

#### **"Cannot delete movie. It may have active showtimes."**
**Solution:**
1. First delete all showtimes for the movie
2. Then delete the movie
3. Or use the archive option instead

#### **"Server error. Please try again later."**
**Solution:**
1. Check if the application is running
2. Verify database connection
3. Check server logs for specific errors
4. Try again in a few minutes

---

## 🔗 **Quick Links**

- **Login (Admin & Users):** `http://localhost:8080/login.html`
- **Movie Management:** `http://localhost:8080/admin-movie-management.html`
- **Movies Page:** `http://localhost:8080/movies.html`
- **Main App:** `http://localhost:8080/index.html`

---

## 📞 **Support**

If you encounter any issues:
1. Check the browser console for error details
2. Verify admin credentials are correct
3. Ensure the application is running on port 8080
4. Check database connection and data integrity

**The Movie Management system is now fully secured and ready for admin use!** 🎬✨
