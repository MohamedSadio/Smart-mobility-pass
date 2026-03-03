/**
 * Mobility Pass Entity Types
 */

/**
 * @typedef {Object} MobilityPass
 * @property {string} id
 * @property {string} userId
 * @property {string} passNumber
 * @property {number} balance
 * @property {string} status - 'ACTIVE', 'INACTIVE', 'SUSPENDED'
 * @property {string} subscriptionType - 'STANDARD', 'PREMIUM', 'ELITE'
 * @property {number} loyaltyPoints
 * @property {string} [createdAt]
 * @property {string} [expirationDate]
 */

export const defaultMobilityPass = {
    id: '',
    userId: '',
    passNumber: '',
    balance: 0,
    status: 'ACTIVE',
    subscriptionType: 'STANDARD',
    loyaltyPoints: 0
};

export const passStatusMap = {
    ACTIVE: 'Actif',
    INACTIVE: 'Inactif',
    SUSPENDED: 'Suspendu'
};

export const subscriptionTypeMap = {
    STANDARD: 'Standard',
    PREMIUM: 'Premium',
    ELITE: 'Elite'
};
