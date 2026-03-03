import React, { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

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

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);

    // Load user & token from localStorage on initial render
    useEffect(() => {
        const storedToken = localStorage.getItem('smp_token');
        const storedUserStr = localStorage.getItem('smp_user');

        console.log('AuthContext init - storedToken:', storedToken ? 'EXISTS' : 'NOT FOUND');
        console.log('AuthContext init - storedUserStr:', storedUserStr);

        if (storedToken && storedUserStr) {
            setToken(storedToken);
            try {
                let userData = JSON.parse(storedUserStr);
                console.log('Parsed user from localStorage:', userData);
                
                // If userId is missing, decode it from the token
                if (!userData.userId) {
                    const tokenPayload = decodeToken(storedToken);
                    console.log('Token payload:', tokenPayload);
                    if (tokenPayload?.userId) {
                        userData.userId = tokenPayload.userId;
                        console.log('Added userId from token:', userData.userId);
                    }
                }
                
                setUser(userData);
            } catch (e) {
                console.error("Failed to parse stored user", e);
            }
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            const response = await authService.login(email, password);
            
            if (response.success && response.user && response.token) {
                setToken(response.token);
                setUser(response.user);
                localStorage.setItem('smp_token', response.token);
                localStorage.setItem('smp_user', JSON.stringify(response.user));

                return { 
                    success: true, 
                    user: response.user,
                    token: response.token
                };
            } else {
                return { 
                    success: false, 
                    error: 'Erreur lors de la connexion'
                };
            }
        } catch (error) {
            console.error('Login catch error:', error);
            const errorMsg = error.response?.data?.message || 
                           error.response?.data?.error ||
                           error.message ||
                           'Erreur de connexion. Vérifiez vos identifiants.';
            return {
                success: false,
                error: errorMsg
            };
        }
    };

    const register = async (userData) => {
        try {
            const response = await authService.register(userData);
            
            if (response.success && response.user) {
                return { 
                    success: true, 
                    user: response.user
                };
            } else {
                return {
                    success: false,
                    error: response.error || 'Erreur lors de l\'inscription'
                };
            }
        } catch (error) {
            const errorMsg = error.response?.data?.message || 
                           error.response?.data?.error ||
                           'Erreur lors de l\'inscription. Vérifiez vos informations.';
            return {
                success: false,
                error: errorMsg
            };
        }
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        localStorage.removeItem('smp_token');
        localStorage.removeItem('smp_user');
        authService.logout();
    };

    const value = {
        user,
        token,
        loading,
        login,
        logout,
        register,
        isAuthenticated: !!token
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

