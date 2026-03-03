/**
 * Trip Entity Types
 */

/**
 * @typedef {Object} Trip
 * @property {string} id
 * @property {string} userId
 * @property {string} transportType - 'BUS', 'BRT', 'TER'
 * @property {number} distanceKm
 * @property {number} baseFare
 * @property {number} discount
 * @property {number} finalFare
 * @property {string} status - 'COMPLETED', 'PAID', 'PENDING', 'CANCELLED'
 * @property {string} [createdAt]
 * @property {string} [completedAt]
 */

/**
 * @typedef {Object} SimulateTripPayload
 * @property {string} userId
 * @property {string} transportType
 * @property {number} distanceKm
 */

export const defaultTrip = {
    id: '',
    userId: '',
    transportType: 'BUS',
    distanceKm: 0,
    baseFare: 0,
    discount: 0,
    finalFare: 0,
    status: 'PENDING'
};

export const transportTypeMap = {
    BUS: 'Bus Urbain',
    BRT: 'BRT (Bus Rapide)',
    TER: 'TER (Train Express)'
};

export const tripStatusMap = {
    COMPLETED: 'Complété',
    PAID: 'Payé',
    PENDING: 'En attente',
    CANCELLED: 'Annulé'
};
