import { api } from '../api/axios';

/**
 * Trip Management Service
 * Handles all API calls related to trips
 * Routes through API Gateway: /trip-management/** → trip-management-service
 */

export const tripService = {
    /**
     * Fetch all trips for a specific user
     * Route: GET /trip-management/api/trips/user/{userId}
     * @param {string} userId
     * @returns {Promise<Array>} List of trips
     */
    async getUserTrips(userId) {
        if (!userId) {
            throw new Error('User ID is required');
        }
        try {
            console.log('Fetching trips for userId:', userId);
            const response = await api.get(`/trip-management/api/trips/user/${userId}`);
            console.log('Trips fetched:', response.data);
            return response.data || [];
        } catch (error) {
            console.error('Error fetching user trips:', error);
            throw error;
        }
    },

    /**
     * Register and validate a new trip
     * Route: POST /trip-management/api/trips
     * @param {Object} tripData - Trip registration data
     * @param {string} tripData.passNumber - Mobile pass number
     * @param {string} tripData.transportType - 'BUS', 'BRT', 'TER'
     * @param {string} tripData.startStation - Departure station
     * @param {string} tripData.endStation - Arrival station
     * @returns {Promise<Object>} Trip response with fare details and status
     */
    async registerTrip(tripData) {
        const { passNumber, transportType, startStation, endStation } = tripData;

        if (!passNumber || !transportType || !startStation || !endStation) {
            throw new Error('Missing required fields: passNumber, transportType, startStation, endStation');
        }

        try {
            console.log('Registering trip:', tripData);
            const response = await api.post('/trip-management/api/trips', {
                passNumber,
                transportType,
                startStation,
                endStation
            });
            console.log('Trip response:', response.data);
            return response.data;
        } catch (error) {
            console.error('Error registering trip:', error);
            throw error;
        }
    },

    /**
     * Get trip history by pass number
     * Route: GET /trip-management/api/trips/pass/{passNumber}
     * @param {string} passNumber
     * @returns {Promise<Array>} List of trips for this pass
     */
    async getTripsByPassNumber(passNumber) {
        if (!passNumber) {
            throw new Error('Pass number is required');
        }
        try {
            const response = await api.get(`/trip-management/api/trips/pass/${passNumber}`);
            return response.data || [];
        } catch (error) {
            console.error('Error fetching trips by pass number:', error);
            throw error;
        }
    }
};

export default tripService;
