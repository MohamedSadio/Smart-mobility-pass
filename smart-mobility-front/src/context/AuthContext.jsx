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
            // --- TEMPORARY MOCK LOGIN ---
            if (email === 'admin@test.com' && password === 'password') {
                const mockToken = "mock_jwt_token_12345";
                const mockUser = {
                    id: "uuid-1234-5678",
                    firstName: "Admin",
                    lastName: "Test",
                    email: "admin@test.com",
                    role: "ADMIN"
                };

                setToken(mockToken);
                setUser(mockUser);
                localStorage.setItem('smp_token', mockToken);
                localStorage.setItem('smp_user', JSON.stringify(mockUser));

                return { success: true };
            }
            // --- END MOCK LOGIN ---
            // Simulated response depending on API Gateway capabilities
            // Once your gateway/auth service is fully implemented, this endpoint should return a JWT
            const response = await api.post('/api/auth/login', { email, password });

            const { token, user: userData } = response.data;

            setToken(token);
            setUser(userData);

            localStorage.setItem('smp_token', token);
            localStorage.setItem('smp_user', JSON.stringify(userData));

            return { success: true };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Login failed'
            };
        }
    };

    const register = async (userData) => {
        try {
            // Send data to user-mobility-pass-service through Gateway
            const response = await api.post('/api/users', userData);
            return { success: true, data: response.data };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Registration failed'
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
