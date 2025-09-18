// script.js - Shared JS for frontend with modern features

// Global variables
let currentUser = null;
let authToken = null;
let basicAuth = null;

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
  initializeAuth();
  initializeNavigation();
});

// Dark mode functionality removed - keeping only dark mode for cinema theme

// Authentication management
function initializeAuth() {
  authToken = null;
  basicAuth = localStorage.getItem('authBasic');
  currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null');

  if (basicAuth && currentUser) {
    updateNavigationForLoggedInUser();
  }

  // Clean up legacy tokens
  localStorage.removeItem('authToken');
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
  nav.className = 'navbar navbar-expand-lg';

  const adminLink = currentUser && currentUser.role === 'ADMIN'
    ? '<a class="nav-link" href="admin.html">Admin</a>'
    : '';

  const authLinks = currentUser ? `
          <a class="nav-link" href="booking.html">Book Tickets</a>
          <a class="nav-link" href="profile.html">Profile</a>
          ${adminLink}
          <a class="nav-link logout-link" href="#" onclick="logout()">Logout (${currentUser.userName})</a>
        ` : `
          <a class="nav-link" href="login.html">Login</a>
          <a class="nav-link" href="register.html">Register</a>
        `;

  nav.innerHTML = `
    <div class="container">
      <a class="navbar-brand" href="index.html">FlixMate</a>
      <div class="navbar-nav ms-auto">
        <a class="nav-link" href="index.html">Home</a>
        <a class="nav-link" href="movies.html">Movies</a>
        <a class="nav-link" href="reviews.html">Reviews</a>
        ${authLinks}
      </div>
    </div>
  `;

  document.body.insertBefore(nav, document.body.firstChild);
}

function updateNavigationForLoggedInUser() {
  const navLinks = document.querySelectorAll('.nav-link');
  navLinks.forEach(link => {
    if (link.textContent.includes('Login') || link.textContent.includes('Register')) {
      link.style.display = 'none';
    }
  });
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
    } else if (basicAuth) {
      headers.append('Authorization', `Basic ${basicAuth}`);
      console.log('Using Basic auth');
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
      
      const errorMessage = `API Error ${response.status}: ${errorData.message || response.statusText}`;
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
    authToken = null;
    basicAuth = btoa(email + ':' + password);
    currentUser = {
      email: user.email,
      userName: user.userName,
      phone: user.phone || '',
      statusName: user.statusName || 'USER',
      role: user.role || 'USER'
    };

    localStorage.setItem('authBasic', basicAuth);
    localStorage.setItem('currentUser', JSON.stringify(currentUser));
    localStorage.removeItem('authToken');

    console.log('Login successful for user:', currentUser);
    showAlert('success', 'Login successful!');
    return true;
  } catch (error) {
    console.error('Login failed:', error);
    showAlert('error', 'Login failed. Please check your credentials.');
    return false;
  }
}


async function register(userData) {
  try {
    const response = await apiCall('/register', 'POST', userData, false);
    showAlert('success', 'Registration successful! Please login.');
    return true;
  } catch (error) {
    showAlert('error', 'Registration failed. Please try again.');
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
















