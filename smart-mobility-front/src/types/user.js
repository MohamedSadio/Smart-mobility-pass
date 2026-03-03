/**
 * User Entity Types
 */

/**
 * @typedef {Object} User
 * @property {string} id
 * @property {string} firstName
 * @property {string} lastName
 * @property {string} email
 * @property {string} phoneNumber
 * @property {string} role
 * @property {string} [createdAt]
 * @property {string} [updatedAt]
 */

/**
 * @typedef {Object} RegisterPayload
 * @property {string} firstName
 * @property {string} lastName
 * @property {string} email
 * @property {string} password
 * @property {string} phoneNumber
 * @property {string} role
 */

/**
 * @typedef {Object} AuthResult
 * @property {boolean} success
 * @property {User} [user]
 * @property {string} [token]
 * @property {string} [error]
 */

export const defaultUser = {
    id: '',
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    role: 'USER'
};

export const defaultRegisterPayload = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phoneNumber: '',
    role: 'USER'
};
