import { api } from '../api/axios';

/**
 * Decode JWT token to extract userId and other claims
 */
const decodeToken = (token) => {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map((c) => {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (error) {
        console.error('Error decoding token:', error);
        return null;
    }
};

/**
 * Authentication Service
 * Handles login, registration, and token management
 */

export const authService = {
    /**
     * Login user with email and password
     * Routes to: /auth/api/auth/login (resolves via API Gateway)
     * @param {string} email
     * @param {string} password
     * @returns {Promise<Object>} User and token data
     */
    async login(email, password) {
        if (!email || !password) {
            throw new Error('Email and password are required');
        }
        try {
            const response = await api.post('/auth/api/auth/login', {
                email,
                password
            });
            
            const responseData = response.data;
            console.log('Login Response:', responseData);
            
            // Backend returns: { token, type, email, role, expiresIn }
            // We need to restructure it to match frontend expectations: { token, user, success }
            const token = responseData.token;
            
            // Decode token to extract userId and other claims
            const tokenPayload = decodeToken(token);
            console.log('Token Payload:', tokenPayload);
            
            const user = {
                userId: tokenPayload?.userId || null,
                email: responseData.email,
                role: responseData.role,
                expiresIn: responseData.expiresIn
            };
            
            return {
                token: token,
                user: user,
                success: true
            };
        } catch (error) {
            console.error('Login error:', error);
            console.error('Error response:', error.response?.data);
            throw error;
        }
    },

    /**
     * Register a new user
     * Routes to: /auth/api/auth/register (resolves via API Gateway)
     * @param {Object} userData - User registration data
     * @param {string} userData.firstName
     * @param {string} userData.lastName
     * @param {string} userData.email
     * @param {string} userData.password
     * @param {string} userData.phoneNumber
     * @param {string} [userData.role] - Default: 'USER'
     * @returns {Promise<Object>} Created user data
     */
    async register(userData) {
        const { firstName, lastName, email, password, phoneNumber, role = 'USER' } = userData;

        if (!firstName || !lastName || !email || !password || !phoneNumber) {
            throw new Error('All user fields are required');
        }

        try {
            const response = await api.post('/auth/api/auth/register', {
                firstName,
                lastName,
                email,
                password,
                phoneNumber,
                role
            });
            
            // Backend returns a UserDTO on successful registration
            const registeredUser = response.data;
            
            return {
                success: true,
                user: registeredUser,
                message: 'Inscription réussie'
            };
        } catch (error) {
            console.error('Registration error:', error);
            const errorMsg = error.response?.data?.message || 
                           error.response?.data?.error ||
                           'Erreur lors de l\'inscription';
            
            return {
                success: false,
                error: errorMsg
            };
        }
    },

    /**
     * Logout user (clear tokens)
     */
    logout() {
        localStorage.removeItem('smp_token');
        localStorage.removeItem('smp_user');
    }
};

export default authService;
