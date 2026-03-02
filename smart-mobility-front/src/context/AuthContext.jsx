import React, { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../api/axios';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);

    // Load user & token from localStorage on initial render
    useEffect(() => {
        const storedToken = localStorage.getItem('smp_token');
        const storedUserStr = localStorage.getItem('smp_user');

        if (storedToken && storedUserStr) {
            setToken(storedToken);
            try {
                setUser(JSON.parse(storedUserStr));
            } catch (e) {
                console.error("Failed to parse stored user", e);
            }
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            // Check if user exists using the User service (via Gateway)
            const response = await api.get(`/user-mobility-pass/api/users/email/${email}`);
            const userData = response.data;

            if (userData && userData.id) {
                // Since there is no backend authentication mechanism, we mock the token locally 
                // but use the REAL user data from the database.
                const mockToken = `mock_token_${userData.id}_${Date.now()}`;

                setToken(mockToken);
                setUser(userData);
                localStorage.setItem('smp_token', mockToken);
                localStorage.setItem('smp_user', JSON.stringify(userData));

                return { success: true };
            } else {
                return { success: false, error: 'Utilisateur introuvable avec cette adresse email.' };
            }
        } catch (error) {
            return {
                success: false,
                error: error.response?.status === 404
                    ? "Aucun compte trouvé pour cet email. Veuillez vous inscrire."
                    : error.response?.data?.message || 'Erreur de connexion. Vérifiez le réseau ou les services backend.'
            };
        }
    };

    const register = async (userData) => {
        try {
            // Send data to user-mobility-pass-service through Gateway
            const response = await api.post('/user-mobility-pass/api/users', userData);
            return { success: true, data: response.data };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || "L'inscription a échoué. Vérifiez vos informations."
            };
        }
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        localStorage.removeItem('smp_token');
        localStorage.removeItem('smp_user');
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

export const useAuth = () => useContext(AuthContext);
