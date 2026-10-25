// ═══════════════════════════════════════════════════════════════
// API HELPER — Central place for all backend calls
// ═══════════════════════════════════════════════════════════════

const API_BASE_URL = 'http://localhost:8080/api';

// ─────────────────────────────────────────────────────────────
// Get JWT token from localStorage
// ─────────────────────────────────────────────────────────────
function getToken() {
    return localStorage.getItem('token');
}

// ─────────────────────────────────────────────────────────────
// Get current user info from localStorage
// ─────────────────────────────────────────────────────────────
function getCurrentUser() {
    return {
        name: localStorage.getItem('name'),
        email: localStorage.getItem('email'),
        role: localStorage.getItem('role')
    };
}

// ─────────────────────────────────────────────────────────────
// Save user info after login
// ─────────────────────────────────────────────────────────────
function saveUser(data) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('name', data.name);
    localStorage.setItem('email', data.email);
    localStorage.setItem('role', data.role);
}

// ─────────────────────────────────────────────────────────────
// Clear user info on logout
// ─────────────────────────────────────────────────────────────
function clearUser() {
    localStorage.removeItem('token');
    localStorage.removeItem('name');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
}

// ─────────────────────────────────────────────────────────────
// Check if user is logged in
// ─────────────────────────────────────────────────────────────
function isLoggedIn() {
    return !!getToken();
}

// ─────────────────────────────────────────────────────────────
// Require specific role — redirect if not
// ─────────────────────────────────────────────────────────────
function requireRole(expectedRole) {
    if (!isLoggedIn()) {
        window.location.href = '/login.html';
        return false;
    }
    const role = localStorage.getItem('role');
    if (role !== expectedRole) {
        alert('Access denied: ' + expectedRole + ' only');
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

// ─────────────────────────────────────────────────────────────
// Core fetch wrapper — handles auth, JSON, errors
// ─────────────────────────────────────────────────────────────
async function apiCall(endpoint, options = {}) {
    const url = endpoint.startsWith('http') ? endpoint : API_BASE_URL + endpoint;

    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    // Add JWT token if present
    const token = getToken();
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    const config = {
        method: options.method || 'GET',
        headers,
        ...(options.body ? { body: JSON.stringify(options.body) } : {})
    };

    try {
        const response = await fetch(url, config);

        // Handle 401 — token expired or invalid
        if (response.status === 401) {
            clearUser();
            window.location.href = '/login.html';
            throw new Error('Session expired. Please login again.');
        }

        // Handle 403 — wrong role
        if (response.status === 403) {
            throw new Error('Access denied: You do not have permission');
        }

        // Parse response
        const data = await response.json();

        if (!response.ok) {
            const message = data.message || data.error || 'Request failed';
            const details = data.details ? data.details.join(', ') : '';
            throw new Error(details ? message + ': ' + details : message);
        }

        return data;

    } catch (error) {
        if (error.name === 'TypeError') {
            throw new Error('Cannot reach server. Is the backend running?');
        }
        throw error;
    }
}

// ─────────────────────────────────────────────────────────────
// Shortcut methods — use these for cleaner code
// ─────────────────────────────────────────────────────────────
const api = {
    get:    (endpoint)          => apiCall(endpoint),
    post:   (endpoint, body)    => apiCall(endpoint, { method: 'POST', body }),
    put:    (endpoint, body)    => apiCall(endpoint, { method: 'PUT', body }),
    delete: (endpoint)          => apiCall(endpoint, { method: 'DELETE' })
};

// ─────────────────────────────────────────────────────────────
// Utility: show a toast/alert message
// ─────────────────────────────────────────────────────────────
function showMessage(message, type = 'info') {
    const colors = {
        success: '#10b981',
        error:   '#ef4444',
        info:    '#3b82f6',
        warning: '#f59e0b'
    };

    const toast = document.createElement('div');
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 14px 20px;
        background: ${colors[type]};
        color: white;
        border-radius: 8px;
        font-family: Arial, sans-serif;
        font-size: 14px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 9999;
        max-width: 350px;
        animation: slideIn 0.3s ease;
    `;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.3s';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// ─────────────────────────────────────────────────────────────
// Utility: logout
// ─────────────────────────────────────────────────────────────
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        clearUser();
        window.location.href = '/login.html';
    }
}

// ─────────────────────────────────────────────────────────────
// Utility: format date
// ─────────────────────────────────────────────────────────────
function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// ─────────────────────────────────────────────────────────────
// Utility: format currency (for package amount)
// ─────────────────────────────────────────────────────────────
function formatPackage(amount) {
    if (!amount) return 'N/A';
    // Convert to LPA (Lakhs Per Annum) if large
    if (amount >= 100000) {
        return '₹' + (amount / 100000).toFixed(1) + ' LPA';
    }
    return '₹' + amount.toLocaleString('en-IN');
}

// ─────────────────────────────────────────────────────────────
// Utility: escape HTML to prevent XSS
// ─────────────────────────────────────────────────────────────
function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// ─────────────────────────────────────────────────────────────
// Utility: status badge color
// ─────────────────────────────────────────────────────────────
function statusColor(status) {
    const colors = {
        'APPLIED':             'badge-blue',
        'SHORTLISTED':         'badge-yellow',
        'INTERVIEW_SCHEDULED': 'badge-purple',
        'SELECTED':            'badge-green',
        'REJECTED':            'badge-red'
    };
    return colors[status] || 'badge-gray';
}