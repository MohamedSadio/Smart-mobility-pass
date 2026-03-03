import { api } from '../api/axios';

/**
 * Notification Service
 * Handles all API calls related to user notifications
 */

export const notificationService = {
    /**
     * Get all notifications for a user
     * @param {string} userId - UUID of the user
     * @returns {Promise<Array>} List of notifications
     */
    async getAllNotifications(userId) {
        if (!userId) {
            throw new Error('User ID is required');
        }
        try {
            const response = await api.get(`/notification/api/notifications/user/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching notifications:', error);
            throw error;
        }
    },

    /**
     * Get unread notifications for a user
     * @param {string} userId - UUID of the user
     * @returns {Promise<Array>} List of unread notifications
     */
    async getUnreadNotifications(userId) {
        if (!userId) {
            throw new Error('User ID is required');
        }
        try {
            const response = await api.get(`/notification/api/notifications/user/${userId}/unread`);
            return response.data;
        } catch (error) {
            console.error('Error fetching unread notifications:', error);
            throw error;
        }
    },

    /**
     * Count unread notifications for a user
     * @param {string} userId - UUID of the user
     * @returns {Promise<number>} Count of unread notifications
     */
    async countUnreadNotifications(userId) {
        if (!userId) {
            throw new Error('User ID is required');
        }
        try {
            const response = await api.get(`/notification/api/notifications/user/${userId}/unread/count`);
            return response.data.count;
        } catch (error) {
            console.error('Error counting unread notifications:', error);
            throw error;
        }
    },

    /**
     * Mark a single notification as read
     * @param {string} notificationId - UUID of the notification
     * @returns {Promise<Object>} Updated notification data
     */
    async markAsRead(notificationId) {
        if (!notificationId) {
            throw new Error('Notification ID is required');
        }
        try {
            const response = await api.patch(`/notification/api/notifications/${notificationId}/read`);
            return response.data;
        } catch (error) {
            console.error('Error marking notification as read:', error);
            throw error;
        }
    },

    /**
     * Mark all notifications as read for a user
     * @param {string} userId - UUID of the user
     * @returns {Promise<void>}
     */
    async markAllAsRead(userId) {
        if (!userId) {
            throw new Error('User ID is required');
        }
        try {
            await api.patch(`/notification/api/notifications/user/${userId}/read-all`);
        } catch (error) {
            console.error('Error marking all notifications as read:', error);
            throw error;
        }
    }
};

export default notificationService;
