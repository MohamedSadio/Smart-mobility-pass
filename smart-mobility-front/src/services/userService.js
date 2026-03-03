import { api } from '../api/axios';

/**
 * User Service
 * Handles all API calls related to user management
 */

export const userService = {
    /**
     * Get all users
     * @returns {Promise<Array>} List of all users
     */
    async getAllUsers() {
        try {
            const response = await api.get('/user-mobility-pass/api/users');
            return response.data;
        } catch (error) {
            console.error('Error fetching users:', error);
            throw error;
        }
    },

    /**
     * Get user by ID
     * @param {string} userId - UUID of the user
     * @returns {Promise<Object>} User data
     */
    async getUserById(userId) {
        if (!userId) {
            throw new Error('User ID is required');
        }
        try {
            const response = await api.get(`/user-mobility-pass/api/users/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching user:', error);
            throw error;
        }
    },

    /**
     * Get user by email
     * @param {string} email - Email address
     * @returns {Promise<Object>} User data
     */
    async getUserByEmail(email) {
        if (!email) {
            throw new Error('Email is required');
        }
        try {
            const response = await api.get(`/user-mobility-pass/api/users/email/${email}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching user by email:', error);
            throw error;
        }
    },

    /**
     * Search users
     * @param {string} term - Search term
     * @returns {Promise<Array>} List of matching users
     */
    async searchUsers(term) {
        if (!term) {
            throw new Error('Search term is required');
        }
        try {
            const response = await api.get(`/user-mobility-pass/api/users/search?term=${encodeURIComponent(term)}`);
            return response.data;
        } catch (error) {
            console.error('Error searching users:', error);
            throw error;
        }
    },

    /**
     * Update user
     * @param {string} userId - UUID of the user
     * @param {Object} userData - User data to update
     * @returns {Promise<Object>} Updated user data
     */
    async updateUser(userId, userData) {
        if (!userId) {
            throw new Error('User ID is required');
        }
        try {
            const response = await api.put(`/user-mobility-pass/api/users/${userId}`, userData);
            return response.data;
        } catch (error) {
            console.error('Error updating user:', error);
            throw error;
        }
    },

    /**
     * Update user status
     * @param {string} userId - UUID of the user
     * @param {string} status - New status (ACTIVE, INACTIVE, etc.)
     * @returns {Promise<Object>} Updated user data
     */
    async updateUserStatus(userId, status) {
        if (!userId || !status) {
            throw new Error('User ID and status are required');
        }
        try {
            const response = await api.patch(`/user-mobility-pass/api/users/${userId}/status?status=${status}`);
            return response.data;
        } catch (error) {
            console.error('Error updating user status:', error);
            throw error;
        }
    },

    /**
     * Delete user
     * @param {string} userId - UUID of the user
     * @returns {Promise<Object>} Deleted user data
     */
    async deleteUser(userId) {
        if (!userId) {
            throw new Error('User ID is required');
        }
        try {
            const response = await api.delete(`/user-mobility-pass/api/users/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Error deleting user:', error);
            throw error;
        }
    },

    /**
     * Create new user
     * @param {Object} userData - User data
     * @returns {Promise<Object>} Created user data
     */
    async createUser(userData) {
        if (!userData) {
            throw new Error('User data is required');
        }
        try {
            const response = await api.post('/user-mobility-pass/api/users', userData);
            return response.data;
        } catch (error) {
            console.error('Error creating user:', error);
            throw error;
        }
    }
};

export default userService;
