// CineMagic Admin Common JS
const API_BASE = '';

function getAdminToken() {
    return localStorage.getItem('admin_token');
}

function checkAdminAuth() {
    const token = getAdminToken();
    const isLoginPage = window.location.pathname.endsWith('login.html');
    if (!token && !isLoginPage) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

async function apiFetch(endpoint, options = {}) {
    const token = getAdminToken();
    const headers = options.headers || {};

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    if (!(options.body instanceof FormData) && !headers['Content-Type']) {
        headers['Content-Type'] = 'application/json';
    }

    const response = await fetch(API_BASE + endpoint, {
        ...options,
        headers
    });

    if (response.status === 401 || response.status === 403) {
        localStorage.removeItem('admin_token');
        localStorage.removeItem('admin_user');
        window.location.href = 'login.html';
        throw new Error('Session expired or unauthorized');
    }

    return response;
}

function logout() {
    localStorage.removeItem('admin_token');
    localStorage.removeItem('admin_user');
    window.location.href = 'login.html';
}

function initAdminNav(activeNavId) {
    const userStr = localStorage.getItem('admin_user');
    if (userStr) {
        try {
            const user = JSON.parse(userStr);
            const userBadge = document.getElementById('adminUserBadge');
            if (userBadge) {
                userBadge.textContent = user.fullName || user.username || 'Admin';
            }
        } catch (e) {}
    }

    if (activeNavId) {
        const link = document.getElementById(activeNavId);
        if (link) {
            link.classList.add('active');
        }
    }
}
