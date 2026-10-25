// ═══════════════════════════════════════════════════════════
// AUTH LOGIC — Handles login and registration
// ═══════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────
// Redirect based on role
// ─────────────────────────────────────────────────────────────
function redirectByRole(role) {
    if (role === 'ADMIN') {
        window.location.href = '/admin/dashboard.html';
    } else if (role === 'STUDENT') {
        window.location.href = '/student/dashboard.html';
    } else {
        window.location.href = '/';
    }
}

// ─────────────────────────────────────────────────────────────
// If already logged in, redirect to dashboard
// ─────────────────────────────────────────────────────────────
(function checkAlreadyLoggedIn() {
    if (isLoggedIn()) {
        const role = localStorage.getItem('role');
        redirectByRole(role);
    }
})();

// ─────────────────────────────────────────────────────────────
// LOGIN
// ─────────────────────────────────────────────────────────────
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        const btn = document.getElementById('loginBtn');
        const errorBox = document.getElementById('errorBox');

        errorBox.style.display = 'none';
        btn.disabled = true;
        btn.textContent = 'Signing in...';

        try {
            const response = await api.post('/auth/login', { email, password });

            // Response shape:
            // { success: true, message: '...', data: { token, email, name, role } }
            saveUser(response.data);

            showMessage('Login successful! Redirecting...', 'success');

            setTimeout(() => redirectByRole(response.data.role), 600);

        } catch (error) {
            errorBox.textContent = error.message;
            errorBox.style.display = 'block';
            btn.disabled = false;
            btn.textContent = 'Sign In';
        }
    });
}

// ─────────────────────────────────────────────────────────────
// REGISTER
// ─────────────────────────────────────────────────────────────
const registerForm = document.getElementById('registerForm');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const name = document.getElementById('name').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        const role = document.getElementById('role').value;

        const btn = document.getElementById('registerBtn');
        const errorBox = document.getElementById('errorBox');
        const successBox = document.getElementById('successBox');

        errorBox.style.display = 'none';
        successBox.style.display = 'none';
        btn.disabled = true;
        btn.textContent = 'Creating account...';

        try {
            await api.post('/auth/register', { name, email, password, role });

            successBox.textContent = 'Account created successfully! Redirecting to login...';
            successBox.style.display = 'block';

            setTimeout(() => {
                window.location.href = '/login.html';
            }, 1500);

        } catch (error) {
            errorBox.textContent = error.message;
            errorBox.style.display = 'block';
            btn.disabled = false;
            btn.textContent = 'Create Account';
        }
    });
}