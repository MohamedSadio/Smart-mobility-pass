import React, { useState, useEffect } from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Home, MapPin, CreditCard, LogOut, User, Activity, Users, BarChart3, Bell } from 'lucide-react';
import notificationService from '../services/notificationService';

const Layout = () => {
    const { user, logout } = useAuth();
    const location = useLocation();
    const [unreadCount, setUnreadCount] = useState(0);

    // Load unread notifications count for users
    useEffect(() => {
        if (user?.userId && user?.role === 'USER') {
            fetchUnreadCount();
            // Refresh every 30 seconds
            const interval = setInterval(fetchUnreadCount, 30000);
            return () => clearInterval(interval);
        }
    }, [user?.userId]);

    const fetchUnreadCount = async () => {
        try {
            const count = await notificationService.countUnreadNotifications(user.userId);
            setUnreadCount(count);
        } catch (err) {
            console.error('Error fetching unread count:', err);
        }
    };

    // Navigation différente selon le rôle
    const userNavigation = [
        { name: 'Mes Trajets', href: '/dashboard/trips', icon: MapPin },
        { name: 'Rechargement', href: '/dashboard/billing', icon: CreditCard },
        { name: 'Notifications', href: '/dashboard/notifications', icon: Bell, badge: unreadCount },
    ];

    const adminNavigation = [
        { name: 'Gestion Utilisateurs', href: '/admin/users', icon: Users },
        { name: 'Statistiques', href: '/admin/stats', icon: BarChart3 },
    ];

    // Sélectionner la navigation selon le rôle
    const navigation = user?.role === 'ADMIN' ? adminNavigation : userNavigation;

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col md:flex-row pb-16 md:pb-0">
            {/* Sidebar */}
            <div className="w-72 bg-white/80 backdrop-blur-xl border-r border-slate-200/60 hidden md:flex flex-col shadow-[4px_0_24px_rgba(0,0,0,0.02)] z-20">
                <div className="h-24 flex items-center px-8 border-b border-slate-100/50">
                    <div className="bg-indigo-600 p-2 rounded-xl mr-3 shadow-lg shadow-indigo-600/20">
                        <Activity className="text-white w-6 h-6" />
                    </div>
                    <span className="text-xl font-extrabold text-slate-800 tracking-tight">Smart Mobility</span>
                </div>

                <div className="flex-1 py-8 px-5 space-y-3 overflow-y-auto mt-4">
                    {navigation.map((item) => {
                        const isActive = location.pathname === item.href;
                        const Icon = item.icon;
                        return (
                            <Link
                                key={item.name}
                                to={item.href}
                                className={`flex items-center justify-between space-x-4 px-5 py-3.5 rounded-2xl transition-all duration-300 group ${isActive
                                    ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/20'
                                    : 'text-slate-500 hover:bg-indigo-50 hover:text-indigo-600'
                                    }`}
                            >
                                <div className="flex items-center space-x-4">
                                    <Icon className={`w-5 h-5 transition-transform duration-300 group-hover:scale-110 ${isActive ? 'text-white' : 'text-slate-400 group-hover:text-indigo-600'}`} />
                                    <span className={`font-semibold ${isActive ? '' : ''}`}>{item.name}</span>
                                </div>
                                {item.badge && item.badge > 0 && (
                                    <span className={`flex-shrink-0 text-xs font-bold px-2 py-1 rounded-full ${
                                        isActive
                                            ? 'bg-white text-indigo-600'
                                            : 'bg-red-500 text-white'
                                    }`}>
                                        {item.badge > 99 ? '99+' : item.badge}
                                    </span>
                                )}
                            </Link>
                        );
                    })}
                </div>

                {/* User Card */}
                <div className="p-6 border-t border-slate-100/50 bg-slate-50/50">
                    <div className="flex items-center space-x-4 mb-5 px-2">
                        <div className="bg-white p-3 rounded-2xl shadow-sm border border-slate-100">
                            <User className="w-6 h-6 text-indigo-600" />
                        </div>
                        <div className="flex-1 min-w-0">
                            <p className="text-sm font-bold text-slate-800 truncate">
                                {user?.firstName} {user?.lastName}
                            </p>
                            <p className="text-xs text-slate-500 truncate font-medium mt-0.5">{user?.email}</p>
                        </div>
                    </div>
                    <button
                        onClick={logout}
                        className="w-full flex items-center justify-center space-x-2 px-4 py-3 text-sm font-bold text-red-600 hover:bg-red-50 hover:text-red-700 rounded-xl transition-all border border-transparent hover:border-red-100"
                    >
                        <LogOut className="w-5 h-5" />
                        <span>Déconnexion</span>
                    </button>
                </div>
            </div>

            {/* Mobile Bottom Navigation */}
            <div className="md:hidden fixed bottom-0 left-0 right-0 bg-white/90 backdrop-blur-lg border-t border-slate-200/60 z-50 flex justify-around items-center h-16 px-4 pb-safe shadow-[0_-4px_24px_rgba(0,0,0,0.02)]">
                {navigation.map((item) => {
                    const isActive = location.pathname === item.href;
                    const Icon = item.icon;
                    return (
                        <Link
                            key={item.name}
                            to={item.href}
                            className={`flex flex-col items-center justify-center w-16 h-full space-y-1 transition-colors relative ${isActive ? 'text-indigo-600' : 'text-slate-400 hover:text-indigo-400'}`}
                        >
                            <div className="relative">
                                <Icon className="w-5 h-5" />
                                {item.badge && item.badge > 0 && (
                                    <span className="absolute -top-2 -right-2 bg-red-500 text-white text-[9px] font-bold px-1.5 py-0.5 rounded-full min-w-[16px] text-center">
                                        {item.badge > 9 ? '9+' : item.badge}
                                    </span>
                                )}
                            </div>
                            <span className="text-[10px] font-bold">{item.name}</span>
                        </Link>
                    )
                })}
            </div>

            {/* Main Content Area */}
            <div className="flex-1 flex flex-col h-screen overflow-hidden relative">
                {/* Mobile Header (Hidden on md+) */}
                <header className="md:hidden h-16 bg-white/80 backdrop-blur-xl border-b border-slate-200/60 flex items-center px-6 justify-between z-40 sticky top-0">
                    <div className="flex items-center">
                        <div className="bg-indigo-600 p-1.5 rounded-lg mr-2">
                            <Activity className="text-white w-4 h-4" />
                        </div>
                        <span className="text-lg font-extrabold text-slate-800">Smart Mobility</span>
                    </div>
                    <div className="flex items-center space-x-4">
                        <span className="text-xs font-bold text-slate-600 bg-slate-100 px-2.5 py-1.5 rounded-lg">{user?.firstName}</span>
                        <button onClick={logout} className="p-2 text-slate-400 hover:text-red-500 transition-colors bg-slate-50 rounded-lg">
                            <LogOut className="w-5 h-5" />
                        </button>
                    </div>
                </header>

                <main className="flex-1 overflow-x-hidden overflow-y-auto p-4 md:p-10 relative z-10 w-full max-w-7xl mx-auto">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default Layout;
