import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from './LoadingSpinner';

/**
 * ProtectedRoute component to guard routes based on authentication and optional roles
 * @param {React.ReactNode} children - Component to render if authorized
 * @param {Array<string>} requiredRoles - Optional array of allowed roles (e.g., ['ADMIN', 'USER'])
 */
const ProtectedRoute = ({ children, requiredRoles = null }) => {
    const { isAuthenticated, user, loading } = useAuth();
    const location = useLocation();

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <LoadingSpinner size="lg" message="Chargement..." />
            </div>
        );
    }

    if (!isAuthenticated) {
        // Redirect to login if not authenticated
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    // Check role if requiredRoles is specified
    if (requiredRoles && !requiredRoles.includes(user?.role)) {
        // Redirect ADMIN users trying to access user routes to /admin
        // Redirect USER users trying to access admin routes to /
        const redirectPath = user?.role === 'ADMIN' ? '/admin' : '/';
        return <Navigate to={redirectPath} replace />;
    }

    return children;
};

export default ProtectedRoute;
