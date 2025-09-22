// Error Boundary System for FlixMate
class ErrorBoundary {
    constructor() {
        this.errorCount = 0;
        this.maxErrors = 5;
        this.errorWindow = 300000; // 5 minutes
        this.errors = [];
        this.init();
    }

    init() {
        // Global error handlers
        window.addEventListener('error', this.handleError.bind(this));
        window.addEventListener('unhandledrejection', this.handlePromiseRejection.bind(this));
        
        // Network error handling
        window.addEventListener('online', this.handleNetworkChange.bind(this));
        window.addEventListener('offline', this.handleNetworkChange.bind(this));
        
        // API error interceptor
        this.interceptFetch();
    }

    handleError(event) {
        const error = {
            type: 'JavaScript Error',
            message: event.message,
            filename: event.filename,
            lineno: event.lineno,
            colno: event.colno,
            stack: event.error?.stack,
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent,
            url: window.location.href
        };
        
        this.logError(error);
        this.showUserFriendlyError('A JavaScript error occurred. Please refresh the page.');
    }

    handlePromiseRejection(event) {
        const error = {
            type: 'Unhandled Promise Rejection',
            message: event.reason?.message || event.reason,
            stack: event.reason?.stack,
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent,
            url: window.location.href
        };
        
        this.logError(error);
        this.showUserFriendlyError('An unexpected error occurred. Please try again.');
    }

    handleNetworkChange(event) {
        if (event.type === 'offline') {
            this.showUserFriendlyError('You are offline. Please check your internet connection.', 'warning');
        } else if (event.type === 'online') {
            this.showUserFriendlyError('You are back online!', 'success');
        }
    }

    interceptFetch() {
        const originalFetch = window.fetch;
        const self = this;
        
        window.fetch = function(...args) {
            return originalFetch.apply(this, args)
                .then(response => {
                    if (!response.ok) {
                        self.handleApiError(response, args[0]);
                    }
                    return response;
                })
                .catch(error => {
                    self.handleNetworkError(error, args[0]);
                    throw error;
                });
        };
    }

    handleApiError(response, url) {
        const error = {
            type: 'API Error',
            status: response.status,
            statusText: response.statusText,
            url: url,
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent
        };
        
        this.logError(error);
        
        // Handle specific HTTP status codes
        switch (response.status) {
            case 401:
                this.handleUnauthorized();
                break;
            case 403:
                this.showUserFriendlyError('Access denied. You do not have permission to perform this action.', 'error');
                break;
            case 404:
                this.showUserFriendlyError('The requested resource was not found.', 'error');
                break;
            case 500:
                this.showUserFriendlyError('Server error. Please try again later.', 'error');
                break;
            default:
                this.showUserFriendlyError(`API Error ${response.status}: ${response.statusText}`, 'error');
        }
    }

    handleNetworkError(error, url) {
        const networkError = {
            type: 'Network Error',
            message: error.message,
            url: url,
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent
        };
        
        this.logError(networkError);
        this.showUserFriendlyError('Network error. Please check your connection and try again.', 'error');
    }

    handleUnauthorized() {
        // Clear authentication data
        localStorage.removeItem('authToken');
        localStorage.removeItem('currentUser');
        
        // Redirect to login
        this.showUserFriendlyError('Your session has expired. Please log in again.', 'error');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
    }

    logError(error) {
        // Clean old errors
        const now = Date.now();
        this.errors = this.errors.filter(e => now - new Date(e.timestamp).getTime() < this.errorWindow);
        
        // Add new error
        this.errors.push(error);
        this.errorCount++;
        
        // Log to console for debugging
        console.error('Error logged:', error);
        
        // Send to server if available
        this.sendErrorToServer(error);
        
        // Check if we should disable the app
        if (this.errorCount > this.maxErrors) {
            this.disableApp();
        }
    }

    async sendErrorToServer(error) {
        try {
            // Only send if we have authentication and the error is not too frequent
            if (localStorage.getItem('authToken') && this.errorCount < 3) {
                await fetch('/api/errors', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                    },
                    body: JSON.stringify(error)
                });
            }
        } catch (e) {
            // Don't log errors when sending errors to avoid infinite loops
            console.warn('Failed to send error to server:', e);
        }
    }

    showUserFriendlyError(message, type = 'error') {
        // Use existing alert system if available
        if (typeof showAlert === 'function') {
            showAlert(type, message);
        } else {
            // Fallback alert system
            this.showFallbackAlert(message, type);
        }
    }

    showFallbackAlert(message, type) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type === 'error' ? 'danger' : type === 'warning' ? 'warning' : 'success'} alert-dismissible fade show`;
        alertDiv.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            max-width: 400px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        `;
        
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(alertDiv);
        
        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.parentNode.removeChild(alertDiv);
            }
        }, 5000);
    }

    disableApp() {
        const errorPage = document.createElement('div');
        errorPage.innerHTML = `
            <div style="
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: #f8f9fa;
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 10000;
                font-family: Arial, sans-serif;
            ">
                <div style="
                    text-align: center;
                    padding: 40px;
                    background: white;
                    border-radius: 8px;
                    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    max-width: 500px;
                ">
                    <h2 style="color: #dc3545; margin-bottom: 20px;">Application Error</h2>
                    <p style="margin-bottom: 20px;">
                        Too many errors have occurred. The application has been disabled to prevent further issues.
                    </p>
                    <button onclick="window.location.reload()" style="
                        background: #007bff;
                        color: white;
                        border: none;
                        padding: 10px 20px;
                        border-radius: 4px;
                        cursor: pointer;
                        font-size: 16px;
                    ">
                        Reload Application
                    </button>
                </div>
            </div>
        `;
        
        document.body.appendChild(errorPage);
    }

    getErrorSummary() {
        return {
            totalErrors: this.errorCount,
            recentErrors: this.errors.length,
            errorTypes: this.errors.reduce((acc, error) => {
                acc[error.type] = (acc[error.type] || 0) + 1;
                return acc;
            }, {})
        };
    }
}

// Initialize error boundary when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.errorBoundary = new ErrorBoundary();
});

// Export for use in other scripts
window.ErrorBoundary = ErrorBoundary;
