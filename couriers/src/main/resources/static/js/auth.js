const API_BASE = 'http://localhost:8089/api';
const CLIENT_ID = 'courier-app';

async function login(username, password) {
    const loginRequest = {
        email: username,
        password: password
    };
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginRequest)
        });

        if (response.ok) {
            const data = await response.json();
            if (data.access_token) {
                localStorage.setItem('access_token', data.access_token);
                const payload = JSON.parse(atob(data.access_token.split('.')[1]));
                localStorage.setItem('user_id', payload.sub);

                const roles = payload.realm_access?.roles || [];
                if (roles.includes('COURIER')) {
                    window.location.href = 'courier.html';
                } else {
                    window.location.href = 'user.html';
                }
            }
        } else {
            alert('Ошибка входа! Проверьте логин и пароль.');
        }
    } catch (e) {
        console.error(e);
        alert('Ошибка соединения с сервером');
    }
}

async function apiRequest(endpoint, method = 'GET', body = null) {
    const token = localStorage.getItem('access_token');
    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };

    const config = { method, headers };
    if (body) {
        config.body = JSON.stringify(body);
    }

    const response = await fetch(`${API_BASE}${endpoint}`, config);

    if (response.status === 401) {
        alert('Сессия истекла');
        window.location.href = 'index.html';
        return;
    }
    return response;
}

function logout() {
    localStorage.removeItem('access_token');
    window.location.href = 'index.html';
}

function renderHeader() {
    const token = localStorage.getItem('access_token');
    const headerContainer = document.getElementById('mainHeader');
    if (!headerContainer)
        return;

    let authButtonHtml;

    const isAuthPage = window.location.pathname.includes('index.html') || window.location.pathname.includes('register.html');

    if (token) {
        authButtonHtml = `<button onclick="logout()" class="btn btn-outline-danger">Выйти</button>`;
    } else {
        if (!isAuthPage) {
            authButtonHtml = `<a href="index.html" class="btn btn-primary">Войти</a>`;
        } else {
            authButtonHtml = '';
        }
    }

    headerContainer.innerHTML = `
        <nav class="navbar navbar-light bg-white shadow-sm mb-4">
            <div class="container">
                <a class="navbar-brand fw-bold d-flex align-items-center" href="user.html">
                    Служба доставки
                </a>
                <div>
                    ${authButtonHtml}
                </div>
            </div>
        </nav>
    `;
}
document.addEventListener('DOMContentLoaded', renderHeader);