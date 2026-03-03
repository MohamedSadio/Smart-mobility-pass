import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import { useAuth } from './context/AuthContext';
import Layout from './components/Layout';

import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import TripsPage from './pages/TripsPage';
import BillingPage from './pages/BillingPage';
import NotificationsPage from './pages/NotificationsPage';
import AdminPage from './pages/AdminPage';
import AdminUsersPage from './pages/AdminUsersPage';
import AdminStatsPage from './pages/AdminStatsPage';

/**
 * Component to redirect users based on their role
 */
const RoleBasedRedirect = () => {
  const { user } = useAuth();
  
  if (user?.role === 'ADMIN') {
    return <Navigate to="/admin" replace />;
  }
  
  return <Navigate to="/dashboard/trips" replace />;
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          {/* Root route - redirect based on role */}
          <Route path="/" element={<ProtectedRoute><RoleBasedRedirect /></ProtectedRoute>} />

          {/* User Dashboard Routes */}
          <Route path="/dashboard" element={<ProtectedRoute requiredRoles={['USER']}><Layout /></ProtectedRoute>}>
            <Route index element={<DashboardPage />} />
            <Route path="trips" element={<TripsPage />} />
            <Route path="billing" element={<BillingPage />} />
            <Route path="notifications" element={<NotificationsPage />} />
          </Route>

          {/* Admin Routes */}
          <Route path="/admin" element={<ProtectedRoute requiredRoles={['ADMIN']}><Layout /></ProtectedRoute>}>
            <Route index element={<AdminPage />} />
            <Route path="users" element={<AdminUsersPage />} />
            <Route path="stats" element={<AdminStatsPage />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
