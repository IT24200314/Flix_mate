// script.js - Shared JS for frontend with modern features

// Global variables
let currentUser = null;
let authToken = null;
let basicAuth = null;

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
  initializeAuth();
  initializeNavigation();
  initializeErrorHandling();
  updateNavigationVisibility();
});

// Global error handling
function initializeErrorHandling() {
  // Handle unhandled promise rejections
  window.addEventListener('unhandledrejection', (event) => {
    console.error('Unhandled promise rejection:', event.reason);
    showAlert('error', 'An unexpected error occurred. Please try again.');
    event.preventDefault(); // Prevent the default browser behavior
  });

  // Handle global JavaScript errors
  window.addEventListener('error', (event) => {
    console.error('Global error:', event.error);
    showAlert('error', 'An unexpected error occurred. Please refresh the page.');
  });

  // Handle network errors
  window.addEventListener('offline', () => {
    showAlert('warning', 'You are offline. Some features may not work properly.');
  });

  window.addEventListener('online', () => {
    showAlert('success', 'You are back online.');
  });

  // Check database connection on page load
  checkDatabaseConnection();
}

// Database connection health check
async function checkDatabaseConnection() {
  console.log('🔍 Checking database connection...');
  
  try {
    const response = await fetch('/api/health/database', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const data = await response.json();
    console.log('✅ Database connection status:', data);
    
    if (data.status === 'success') {
      console.log(`✅ Database connected successfully. Found ${data.movieCount} movies.`);
      showAlert('success', `Database connected successfully! Found ${data.movieCount} movies.`);
      
      // Log sample movie data
      if (data.sampleMovie) {
        console.log('📽️ Sample movie:', data.sampleMovie);
      }
    } else {
      console.error('❌ Database connection failed:', data.message);
      showAlert('error', `Database connection failed: ${data.message}`);
    }
    
    return data;
  } catch (error) {
    console.error('❌ Database health check failed:', error);
    showAlert('error', `Database health check failed: ${error.message}`);
    return { status: 'error', message: error.message };
  }
}

// Enhanced API call function with better error handling
async function apiCall(endpoint, method = 'GET', data = null, showAlerts = true) {
  console.log(`🌐 API Call: ${method} ${endpoint}`);
  
  const config = {
    method: method,
    headers: {
      'Content-Type': 'application/json',
    }
  };

  if (authToken) {
    config.headers['Authorization'] = `Bearer ${authToken}`;
  }

  if (data) {
    config.body = JSON.stringify(data);
  }

  try {
    const response = await fetch(endpoint, config);
    console.log(`📡 Response status: ${response.status} ${response.statusText}`);

    if (!response.ok) {
      const errorText = await response.text();
      console.error(`❌ API Error ${response.status}:`, errorText);
      
      if (showAlerts) {
        showAlert('error', `API Error ${response.status}: ${errorText}`);
      }
      
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }

    const result = await response.json();
    console.log('✅ API Response:', result);
    return result;

  } catch (error) {
    console.error('❌ API Call failed:', error);
    
    if (showAlerts) {
      showAlert('error', `API Call failed: ${error.message}`);
    }
    
    throw error;
  }
}

// Dark mode functionality removed - keeping only dark mode for cinema theme

// Authentication management
function initializeAuth() {
  authToken = localStorage.getItem('authToken');
  basicAuth = null; // No longer using Basic Auth
  currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null');

  if (authToken && currentUser) {
    updateNavigationForLoggedInUser();
  }

  // Clean up legacy Basic Auth
  localStorage.removeItem('authBasic');
}

// Navigation management
function initializeNavigation() {
  // Add navigation to pages that don't have it
  const pagesWithoutNav = ['login.html', 'register.html'];
  const currentPage = window.location.pathname.split('/').pop();
  
  if (!pagesWithoutNav.includes(currentPage)) {
    addNavigation();
  }
}

function addNavigation() {
  const nav = document.createElement('nav');
  nav.className = 'navbar navbar-expand-lg scope-cinemas-navbar';

  const adminLink = currentUser && (currentUser.role === 'ADMIN' || currentUser.role === 'admin')
    ? '<li class="nav-item"><a class="nav-link" href="admin-home.html"><i class="fas fa-user-shield me-1"></i>Admin</a></li>'
    : '';

  const authLinks = currentUser ? `
          <li class="nav-item">
            <a class="nav-link" href="booking.html">
              <i class="fas fa-ticket-alt me-1"></i>Book Tickets
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="booking-history.html">
              <i class="fas fa-calendar-check me-1"></i>My Bookings
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="profile.html">
              <i class="fas fa-user me-1"></i>Profile
            </a>
          </li>
          ${adminLink}
          <li class="nav-item">
            <a class="nav-link logout-link" href="#" onclick="logout()">
              <i class="fas fa-sign-out-alt me-1"></i>Logout (${currentUser.userName})
            </a>
          </li>
        ` : `
          <li class="nav-item">
            <a class="nav-link" href="login.html">
              <i class="fas fa-sign-in-alt me-1"></i>Login
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="register.html">
              <i class="fas fa-user-plus me-1"></i>Register
            </a>
          </li>
        `;

  nav.innerHTML = `
    <div class="container">
      <a class="navbar-brand scope-cinemas-brand" href="index.html">
        <i class="fas fa-film me-2"></i>FlixMate
      </a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ms-auto">
          <li class="nav-item">
            <a class="nav-link" href="index.html">
              <i class="fas fa-home me-1"></i>Home
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="movies.html">
              <i class="fas fa-film me-1"></i>Movies
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="reviews.html">
              <i class="fas fa-star me-1"></i>Reviews
            </a>
          </li>
          ${authLinks}
        </ul>
      </div>
    </div>
  `;

  document.body.insertBefore(nav, document.body.firstChild);
}

function updateNavigationForLoggedInUser() {
  // Update navigation based on user role and authentication status
  const navLinks = document.querySelectorAll('.nav-link');
  
  navLinks.forEach(link => {
    const href = link.getAttribute('href');
    
    if (href === 'login.html') {
      link.textContent = 'Logout';
      link.href = '#';
      link.onclick = (e) => {
        e.preventDefault();
        logout();
      };
      link.classList.add('btn-logout');
    } else if (href === 'register.html') {
      link.style.display = 'none';
    } else if (href === 'booking-history.html' || href === 'profile.html') {
      link.style.display = 'block';
    }
  });
  
  // Add admin link if user is admin
  if (isAdmin()) {
    const adminLink = document.createElement('li');
    adminLink.className = 'nav-item';
    adminLink.innerHTML = '<a class="nav-link" href="admin-home.html"><i class="fas fa-cog me-1"></i>Admin</a>';
    
    const navbarNav = document.querySelector('.navbar-nav');
    if (navbarNav && !document.querySelector('.nav-link[href="admin-home.html"]')) {
      navbarNav.appendChild(adminLink);
    }
  }
}

// API call function with enhanced error handling
async function apiCall(endpoint, method = 'GET', body = null, useAuth = true) {
  console.log('=== FRONTEND API CALL START ===');
  console.log('Endpoint:', endpoint);
  console.log('Method:', method);
  console.log('Body:', body);
  console.log('Use Auth:', useAuth);
  
  const headers = new Headers();
  if (body) {
    headers.append('Content-Type', 'application/json');
  }

  // Add authentication
  if (useAuth) {
    if (authToken) {
      headers.append('Authorization', `Bearer ${authToken}`);
      console.log('Using Bearer token auth');
    } else {
      console.log('No authentication available');
    }
  }

  const options = {
    method,
    headers,
    body: body ? JSON.stringify(body) : null
  };
  
  console.log('Request options:', options);
  
  // Show loading indicator
  showLoading(true);
  
  try {
    console.log('Making fetch request to:', `/api${endpoint}`);
    const response = await fetch(`/api${endpoint}`, options);
    
    console.log('Response status:', response.status);
    console.log('Response headers:', Object.fromEntries(response.headers.entries()));
    
    if (!response.ok) {
      console.error('Response not OK. Status:', response.status);
      console.error('Status text:', response.statusText);
      
      let errorData;
      const contentType = response.headers.get('content-type') || '';
      
      try {
        if (contentType.includes('application/json')) {
          errorData = await response.json();
          console.error('Error response body (JSON):', errorData);
        } else {
          const textResponse = await response.text();
          console.error('Error response body (text):', textResponse);
          errorData = { message: textResponse || 'Unknown error' };
        }
      } catch (parseError) {
        console.error('Could not parse error response:', parseError);
        errorData = { message: 'Failed to parse error response' };
      }
      
      // Handle specific HTTP status codes
      let errorMessage;
      if (response.status === 401) {
        // Unauthorized - redirect to login
        console.log('Unauthorized access detected, redirecting to login');
        localStorage.removeItem('authToken');
        localStorage.removeItem('currentUser');
        window.location.href = 'login.html';
        errorMessage = 'Authentication required. Redirecting to login...';
      } else if (response.status === 403) {
        errorMessage = 'Access denied. You do not have permission to perform this action.';
      } else if (response.status === 404) {
        errorMessage = 'Resource not found. Please check your request and try again.';
      } else if (response.status === 409) {
        errorMessage = 'Conflict: The request could not be completed due to a conflict with the current state.';
      } else if (response.status >= 500) {
        errorMessage = 'Server error. Please try again later or contact support if the problem persists.';
      } else {
        errorMessage = `API Error ${response.status}: ${errorData.message || response.statusText}`;
      }
      
      console.error('Final error message:', errorMessage);
      throw new Error(errorMessage);
    }
    
    const contentType = response.headers.get('content-type') || '';
    console.log('Response content type:', contentType);

    if (contentType.includes('application/json')) {
      const jsonData = await response.json();
      console.log('Response data (JSON):', jsonData);
      return jsonData;
    }

    const text = await response.text();
    console.log('Response data (text):', text);
    return text || null;
  } catch (error) {
    console.error('=== FRONTEND API CALL ERROR ===');
    console.error('Error type:', error.constructor.name);
    console.error('Error message:', error.message);
    console.error('Error stack:', error.stack);
    console.error('=== END FRONTEND API CALL ERROR ===');
    
    showAlert('error', `API Error: ${error.message}`);
    throw error;
  } finally {
    showLoading(false);
    console.log('=== FRONTEND API CALL END ===');
  }
}

// Loading indicator
function showLoading(show) {
  let loading = document.getElementById('loading');
  if (!loading) {
    loading = document.createElement('div');
    loading.id = 'loading';
    loading.className = 'loading';
    loading.textContent = 'Loading...';
    document.body.appendChild(loading);
  }
  loading.style.display = show ? 'block' : 'none';
}

// Alert system
function showAlert(type, message, duration = 5000) {
  const alertContainer = document.getElementById('alert-container') || createAlertContainer();
  
  const alert = document.createElement('div');
  alert.className = `alert alert-${type} fade-in`;
  alert.innerHTML = `
    <strong>${type.charAt(0).toUpperCase() + type.slice(1)}:</strong> ${message}
    <button type="button" class="btn-close" onclick="this.parentElement.remove()" style="float: right; background: none; border: none; font-size: 1.2rem; cursor: pointer;">&times;</button>
  `;
  
  alertContainer.appendChild(alert);
  
  // Auto-remove after duration
  setTimeout(() => {
    if (alert.parentElement) {
      alert.remove();
    }
  }, duration);
}

function createAlertContainer() {
  const container = document.createElement('div');
  container.id = 'alert-container';
  container.style.position = 'fixed';
  container.style.top = '80px';
  container.style.right = '20px';
  container.style.zIndex = '9999';
  container.style.maxWidth = '400px';
  document.body.appendChild(container);
  return container;
}

// Authentication functions
async function login(email, password) {
  try {
    console.log('=== FRONTEND LOGIN START ===');
    console.log('Email:', email);
    
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({
        email: email,
        password: password
      })
    });

    const result = await response.json();
    console.log('Login response:', result);

    if (!response.ok || !result.success) {
      throw new Error(result.message || 'Invalid credentials');
    }

    const user = result.user;
    authToken = result.token;
    basicAuth = null; // No longer using Basic Auth
    
    currentUser = {
      email: user.email,
      userName: user.userName,
      phone: user.phone || '',
      statusName: user.statusName || 'USER',
      role: user.role || 'USER',
      userId: user.userId
    };

    localStorage.setItem('authToken', authToken);
    localStorage.setItem('currentUser', JSON.stringify(currentUser));
    localStorage.removeItem('authBasic');

    console.log('Login successful for user:', currentUser);
    showAlert('success', 'Login successful!');
    
    // Redirect admin users to admin dashboard
    if (currentUser.role === 'admin' || currentUser.role === 'ADMIN') {
      setTimeout(() => {
        window.location.href = 'admin-home.html';
      }, 1000);
    }
    
    return true;
  } catch (error) {
    console.error('Login failed:', error);
    showAlert('error', 'Login failed. Please check your credentials.');
    return false;
  }
}


// Logout function
function logout() {
  try {
    console.log('=== FRONTEND LOGOUT ===');
    
    // Clear authentication data
    authToken = null;
    basicAuth = null;
    currentUser = null;
    
    // Clear localStorage
    localStorage.removeItem('authToken');
    localStorage.removeItem('authBasic');
    localStorage.removeItem('currentUser');
    
    // Clear sessionStorage
    sessionStorage.clear();
    
    // Update navigation
    updateNavigationForLoggedOutUser();
    
    console.log('Logout successful');
    showAlert('success', 'Logged out successfully');
    
    // Redirect to login page
    setTimeout(() => {
      window.location.href = 'login.html';
    }, 1000);
    
    return true;
  } catch (error) {
    console.error('Logout error:', error);
    showAlert('error', 'Logout failed');
    return false;
  }
}

// Update navigation for logged out user
function updateNavigationForLoggedOutUser() {
  // Remove login-specific navigation items
  const loginLinks = document.querySelectorAll('.nav-link[href="login.html"], .nav-link[href="register.html"]');
  loginLinks.forEach(link => {
    link.style.display = 'block';
  });
  
  // Hide logout-specific items
  const logoutLinks = document.querySelectorAll('.nav-link[href="booking-history.html"], .nav-link[href="profile.html"]');
  logoutLinks.forEach(link => {
    link.style.display = 'none';
  });
  
  // Update login/logout buttons
  const loginButtons = document.querySelectorAll('.btn-login, .btn-logout');
  loginButtons.forEach(button => {
    if (button.classList.contains('btn-logout')) {
      button.style.display = 'none';
    } else if (button.classList.contains('btn-login')) {
      button.style.display = 'block';
    }
  });
}

// Check if user is authenticated
function isAuthenticated() {
  return authToken && currentUser;
}

// Check if user is admin
function isAdmin() {
  return currentUser && (currentUser.role === 'admin' || currentUser.role === 'ADMIN');
}

// Require authentication for protected pages
function requireAuth() {
  if (!isAuthenticated()) {
    showAlert('error', 'Authentication required. Redirecting to login...');
    setTimeout(() => {
      window.location.href = 'login.html';
    }, 2000);
    return false;
  }
  return true;
}

// Require admin role for admin pages
function requireAdmin() {
  if (!requireAuth()) {
    return false;
  }
  
  if (!isAdmin()) {
    showAlert('error', 'Admin access required. Redirecting to home...');
    setTimeout(() => {
      window.location.href = 'index.html';
    }, 2000);
    return false;
  }
  return true;
}

async function register(userData) {
  try {
    console.log('=== FRONTEND: Starting registration ===');
    console.log('User data:', userData);
    
    const response = await apiCall('/register', 'POST', userData, false);
    console.log('Registration response:', response);
    
    if (response && response.success) {
      showAlert('success', 'Registration successful! Please login.');
      return true;
    } else {
      const errorMessage = response && response.message ? response.message : 'Registration failed. Please try again.';
      showAlert('error', errorMessage);
      return false;
    }
  } catch (error) {
    console.error('=== FRONTEND: Registration error ===');
    console.error('Error:', error);
    
    let errorMessage = 'Registration failed. Please try again.';
    
    if (error.message) {
      errorMessage = error.message;
    } else if (error.response && error.response.message) {
      errorMessage = error.response.message;
    }
    
    showAlert('error', errorMessage);
    return false;
  }
}

function logout() {
  authToken = null;
  basicAuth = null;
  currentUser = null;
  localStorage.removeItem('authToken');
  localStorage.removeItem('authBasic');
  localStorage.removeItem('currentUser');
  showAlert('success', 'Logged out successfully!');
  window.location.href = 'index.html';
}
// Modal functions
function showModal(title, content, onConfirm = null) {
  const modal = document.createElement('div');
  modal.className = 'modal fade show';
  modal.style.display = 'block';
  modal.innerHTML = `
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">${title}</h5>
          <button type="button" class="btn-close" onclick="closeModal(this)">&times;</button>
        </div>
        <div class="modal-body">
          ${content}
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline" onclick="closeModal(this)">Cancel</button>
          ${onConfirm ? '<button type="button" class="btn" onclick="confirmModal(this)">Confirm</button>' : ''}
        </div>
      </div>
    </div>
  `;
  
  modal.onConfirm = onConfirm;
  document.body.appendChild(modal);
  
  // Add backdrop
  const backdrop = document.createElement('div');
  backdrop.className = 'modal-backdrop fade show';
  document.body.appendChild(backdrop);
}

function closeModal(button) {
  const modal = button.closest('.modal');
  const backdrop = document.querySelector('.modal-backdrop');
  modal.remove();
  if (backdrop) backdrop.remove();
}

function confirmModal(button) {
  const modal = button.closest('.modal');
  if (modal.onConfirm) {
    modal.onConfirm();
  }
  closeModal(button);
}

// Utility functions
function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

function formatCurrency(amount) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount);
}

function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

// Seat selection functionality
function initializeSeatSelection() {
  const seats = document.querySelectorAll('.seat');
  seats.forEach(seat => {
    seat.addEventListener('click', function() {
      if (this.classList.contains('occupied')) return;
      
      this.classList.toggle('selected');
      updateSelectedSeats();
    });
  });
}

function updateSelectedSeats() {
  const selectedSeats = document.querySelectorAll('.seat.selected');
  const count = selectedSeats.length;
  const totalPrice = count * 10; // Assuming $10 per seat
  
  const summary = document.getElementById('booking-summary');
  if (summary) {
    summary.innerHTML = `
      <div class="card">
        <div class="card-body">
          <h5>Booking Summary</h5>
          <p>Selected Seats: ${count}</p>
          <p>Total Price: ${formatCurrency(totalPrice)}</p>
          <button class="btn" onclick="proceedToPayment()" ${count === 0 ? 'disabled' : ''}>
            Proceed to Payment
          </button>
        </div>
      </div>
    `;
  }
}

// Animation utilities
function addFadeInAnimation(element) {
  element.classList.add('fade-in');
}

function addSlideInAnimation(element) {
  element.classList.add('slide-in');
}

// Form validation
function validateForm(formId) {
  const form = document.getElementById(formId);
  const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
  let isValid = true;
  
  inputs.forEach(input => {
    if (!input.value.trim()) {
      input.style.borderColor = 'var(--error-color)';
      isValid = false;
    } else {
      input.style.borderColor = 'var(--border-color)';
    }
  });
  
  return isValid;
}

// Local storage helpers
function saveToStorage(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    console.error('Error saving to localStorage:', error);
  }
}

function getFromStorage(key, defaultValue = null) {
  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : defaultValue;
  } catch (error) {
    console.error('Error reading from localStorage:', error);
    return defaultValue;
  }
}

// Navigation management
function updateNavigationVisibility() {
  const isLoggedIn = authToken && currentUser;
  const isAdmin = currentUser && currentUser.role === 'admin';
  
  // Show/hide login/register buttons
  const loginNavItem = document.getElementById('login-nav-item');
  const registerNavItem = document.getElementById('register-nav-item');
  const logoutNavItem = document.getElementById('logout-nav-item');
  const adminNavItem = document.getElementById('admin-nav-item');
  
  if (loginNavItem) {
    loginNavItem.style.display = isLoggedIn ? 'none' : 'block';
  }
  if (registerNavItem) {
    registerNavItem.style.display = isLoggedIn ? 'none' : 'block';
  }
  if (logoutNavItem) {
    logoutNavItem.style.display = isLoggedIn ? 'block' : 'none';
  }
  if (adminNavItem) {
    adminNavItem.style.display = (isLoggedIn && isAdmin) ? 'block' : 'none';
  }
  
  // Update active navigation item based on current page
  updateActiveNavigationItem();
}

function updateActiveNavigationItem() {
  const currentPage = window.location.pathname.split('/').pop() || 'index.html';
  const navLinks = document.querySelectorAll('#navbar-menu .nav-link');
  
  navLinks.forEach(link => {
    link.classList.remove('active');
    const href = link.getAttribute('href');
    if (href && href.includes(currentPage)) {
      link.classList.add('active');
    }
  });
}

// Logout function
function logout() {
  // Clear authentication data
  localStorage.removeItem('authToken');
  localStorage.removeItem('currentUser');
  authToken = null;
  currentUser = null;
  
  // Update navigation
  updateNavigationVisibility();
  
  // Show success message
  showAlert('success', 'Logged out successfully');
  
  // Redirect to home page
  setTimeout(() => {
    window.location.href = 'index.html';
  }, 1000);
}

// Export functions for use in other scripts
window.apiCall = apiCall;
window.showAlert = showAlert;
window.showModal = showModal;
window.formatDate = formatDate;
window.formatCurrency = formatCurrency;
window.validateForm = validateForm;
window.login = login;
window.register = register;
window.logout = logout;
window.updateNavigationVisibility = updateNavigationVisibility;
















