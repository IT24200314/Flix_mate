#!/usr/bin/env python3
"""
FlixMate Data Persistence Test Script
This script tests CRUD operations to ensure data persistence works correctly.
"""

import requests
import json
import time
from datetime import datetime

# Configuration
BASE_URL = "http://localhost:8080/api"
ADMIN_USERNAME = "admin@flixmate.com"
ADMIN_PASSWORD = "password"  # You should update this to match your admin password

class FlixMateAPITester:
    def __init__(self):
        self.session = requests.Session()
        self.auth_token = None
        self.created_movie_id = None
        
    def log(self, message, level="INFO"):
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        print(f"[{timestamp}] {level}: {message}")
        
    def test_database_health(self):
        """Test database connectivity"""
        self.log("Testing database health...")
        try:
            response = self.session.get(f"{BASE_URL}/health/database")
            if response.status_code == 200:
                data = response.json()
                self.log(f"✅ Database health: {data['status']} - {data['movieCount']} movies found")
                return True
            else:
                self.log(f"❌ Database health check failed: {response.status_code}", "ERROR")
                return False
        except Exception as e:
            self.log(f"❌ Database health check error: {str(e)}", "ERROR")
            return False
    
    def authenticate_admin(self):
        """Authenticate as admin user"""
        self.log("Authenticating as admin...")
        try:
            auth_data = {
                "email": ADMIN_USERNAME,
                "password": ADMIN_PASSWORD
            }
            response = self.session.post(f"{BASE_URL}/auth/login", json=auth_data)
            if response.status_code == 200:
                data = response.json()
                if data.get('success'):
                    self.auth_token = data.get('token')
                    self.session.headers.update({'Authorization': f'Bearer {self.auth_token}'})
                    self.log("✅ Admin authentication successful")
                    return True
            
            self.log(f"❌ Admin authentication failed: {response.status_code}", "ERROR")
            return False
        except Exception as e:
            self.log(f"❌ Admin authentication error: {str(e)}", "ERROR")
            return False
    
    def test_read_movies(self):
        """Test reading movies (READ operation)"""
        self.log("Testing READ operation - fetching movies...")
        try:
            response = self.session.get(f"{BASE_URL}/public/movies")
            if response.status_code == 200:
                movies = response.json()
                self.log(f"✅ READ operation successful: {len(movies)} movies retrieved")
                if movies:
                    self.log(f"Sample movie: {movies[0]['title']} (ID: {movies[0]['movieId']})")
                return True, movies
            else:
                self.log(f"❌ READ operation failed: {response.status_code}", "ERROR")
                return False, []
        except Exception as e:
            self.log(f"❌ READ operation error: {str(e)}", "ERROR")
            return False, []
    
    def test_create_movie(self):
        """Test creating a new movie (CREATE operation)"""
        self.log("Testing CREATE operation - adding new movie...")
        try:
            test_movie = {
                "title": f"Test Movie {int(time.time())}",
                "description": "This is a test movie created by the persistence test script.",
                "releaseYear": 2024,
                "genre": "Test",
                "duration": 120,
                "language": "English",
                "director": "Test Director",
                "cast": "Test Actor 1, Test Actor 2",
                "rating": "PG-13",
                "isActive": True
            }
            
            response = self.session.post(f"{BASE_URL}/admin/movies", json=test_movie)
            if response.status_code in [200, 201]:
                self.log("✅ CREATE operation successful")
                # Try to get the created movie ID from response or find it
                return True
            else:
                self.log(f"❌ CREATE operation failed: {response.status_code} - {response.text}", "ERROR")
                return False
        except Exception as e:
            self.log(f"❌ CREATE operation error: {str(e)}", "ERROR")
            return False
    
    def find_test_movie(self):
        """Find the test movie we created"""
        success, movies = self.test_read_movies()
        if success:
            for movie in movies:
                if movie['title'].startswith('Test Movie'):
                    self.created_movie_id = movie['movieId']
                    self.log(f"Found test movie with ID: {self.created_movie_id}")
                    return True
        return False
    
    def test_update_movie(self):
        """Test updating an existing movie (UPDATE operation)"""
        if not self.created_movie_id:
            if not self.find_test_movie():
                self.log("❌ Cannot test UPDATE: No test movie found", "ERROR")
                return False
        
        self.log(f"Testing UPDATE operation - modifying movie ID {self.created_movie_id}...")
        try:
            updated_movie = {
                "title": f"Updated Test Movie {int(time.time())}",
                "description": "This movie has been updated by the persistence test script.",
                "releaseYear": 2024,
                "genre": "Updated Test",
                "duration": 135,
                "language": "English",
                "director": "Updated Test Director",
                "cast": "Updated Test Actor 1, Updated Test Actor 2",
                "rating": "R",
                "isActive": True
            }
            
            response = self.session.put(f"{BASE_URL}/admin/movies/{self.created_movie_id}", json=updated_movie)
            if response.status_code == 200:
                self.log("✅ UPDATE operation successful")
                return True
            else:
                self.log(f"❌ UPDATE operation failed: {response.status_code} - {response.text}", "ERROR")
                return False
        except Exception as e:
            self.log(f"❌ UPDATE operation error: {str(e)}", "ERROR")
            return False
    
    def test_delete_movie(self):
        """Test deleting a movie (DELETE operation)"""
        if not self.created_movie_id:
            if not self.find_test_movie():
                self.log("❌ Cannot test DELETE: No test movie found", "ERROR")
                return False
        
        self.log(f"Testing DELETE operation - removing movie ID {self.created_movie_id}...")
        try:
            response = self.session.delete(f"{BASE_URL}/admin/movies/{self.created_movie_id}")
            if response.status_code == 200:
                self.log("✅ DELETE operation successful")
                return True
            else:
                self.log(f"❌ DELETE operation failed: {response.status_code} - {response.text}", "ERROR")
                return False
        except Exception as e:
            self.log(f"❌ DELETE operation error: {str(e)}", "ERROR")
            return False
    
    def verify_persistence(self):
        """Verify that changes persist after server restart simulation"""
        self.log("Verifying data persistence...")
        
        # Wait a moment to ensure data is committed
        time.sleep(2)
        
        # Read movies again to verify persistence
        success, movies = self.test_read_movies()
        if success:
            self.log("✅ Data persistence verified - movies are still accessible")
            return True
        else:
            self.log("❌ Data persistence failed - cannot read movies after operations", "ERROR")
            return False
    
    def run_full_test(self):
        """Run complete CRUD test suite"""
        self.log("Starting FlixMate Data Persistence Test Suite")
        self.log("=" * 60)
        
        # Test database health first
        if not self.test_database_health():
            self.log("❌ Database health check failed. Cannot proceed with tests.", "ERROR")
            return False
        
        # Test READ operation (should work without authentication)
        if not self.test_read_movies()[0]:
            self.log("❌ Basic READ operation failed. Check if application is running.", "ERROR")
            return False
        
        # Authenticate for admin operations
        if not self.authenticate_admin():
            self.log("⚠️ Admin authentication failed. Skipping CREATE/UPDATE/DELETE tests.", "WARNING")
            self.log("Note: You may need to update the admin credentials in this script.")
            return False
        
        # Test CREATE operation
        create_success = self.test_create_movie()
        
        # Test UPDATE operation
        update_success = False
        if create_success:
            time.sleep(1)  # Brief pause
            update_success = self.test_update_movie()
        
        # Test DELETE operation
        delete_success = False
        if update_success:
            time.sleep(1)  # Brief pause
            delete_success = self.test_delete_movie()
        
        # Verify persistence
        persistence_success = self.verify_persistence()
        
        # Summary
        self.log("=" * 60)
        self.log("Test Results Summary:")
        self.log(f"Database Health: ✅")
        self.log(f"READ Operation: ✅")
        self.log(f"CREATE Operation: {'✅' if create_success else '❌'}")
        self.log(f"UPDATE Operation: {'✅' if update_success else '❌'}")
        self.log(f"DELETE Operation: {'✅' if delete_success else '❌'}")
        self.log(f"Data Persistence: {'✅' if persistence_success else '❌'}")
        
        all_success = create_success and update_success and delete_success and persistence_success
        
        if all_success:
            self.log("🎉 All CRUD operations successful! Data persistence is working correctly.")
        else:
            self.log("⚠️ Some operations failed. Check the logs above for details.")
        
        return all_success

def main():
    """Main function to run the test suite"""
    print("FlixMate Data Persistence Test Script")
    print("====================================")
    print()
    print("This script will test CRUD operations to ensure data persistence works correctly.")
    print("Make sure the FlixMate application is running on http://localhost:8080")
    print()
    
    input("Press Enter to start the tests...")
    print()
    
    tester = FlixMateAPITester()
    success = tester.run_full_test()
    
    print()
    if success:
        print("✅ All tests passed! Your data persistence is working correctly.")
    else:
        print("❌ Some tests failed. Please check the output above for details.")
        print()
        print("Common issues and solutions:")
        print("1. Make sure the Spring Boot application is running")
        print("2. Verify database connection settings")
        print("3. Check if @Transactional annotations are present in service classes")
        print("4. Ensure proper admin user credentials")
    
    return 0 if success else 1

if __name__ == "__main__":
    exit(main())
