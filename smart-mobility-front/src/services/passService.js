import { api } from '../api/axios';

/**
 * Mobility Pass Service
 * Handles all API calls related to mobility passes
 */

export const passService = {
    /**
     * Fetch mobility pass data for a specific user
     * @param {string} userId
     * @returns {Promise<Object>} Mobility pass data
     */
    async getMobilityPass(userId) {
        if (!userId) {
            throw new Error('User ID is required');
        }
        try {
            const response = await api.get(`/user-mobility-pass/api/mobility-passes/user/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching mobility pass:', error);
            throw error;
        }
    },

    /**
     * Recharge mobility pass balance
     * @param {string} passNumber
     * @param {number} amount
     * @returns {Promise<Object>} Updated pass data
     */
    async rechargePass(passNumber, amount) {
        if (!passNumber || !amount) {
            throw new Error('Pass number and amount are required');
        }
        try {
            const response = await api.post('/billing/api/billing/recharge', {
                passNumber,
                amount: parseFloat(amount)
            });
            return response.data;
        } catch (error) {
            console.error('Error recharging pass:', error);
            throw error;
        }
    }
};

export default passService;
