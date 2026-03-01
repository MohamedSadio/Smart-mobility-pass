import axios from 'axios';

// Base API instance pointing to the API Gateway
export const api = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request Interceptor: Attach the JWT token to every request if available
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('smp_token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response Interceptor: Handle generalized errors (like 401 Unauthorized)
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            // If the token is invalid or expired, log out the user
            localStorage.removeItem('smp_token');
            localStorage.removeItem('smp_user');
            // Redirect to login (can be handled via the context/state or window object)
            if (window.location.pathname !== '/login') {
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export default api;
