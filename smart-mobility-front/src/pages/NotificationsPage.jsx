import React, { useState, useEffect } from 'react';
import { Bell, CheckCircle, AlertCircle, Info, Trash2, CheckCheck } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import notificationService from '../services/notificationService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorAlert from '../components/ErrorAlert';

const NotificationsPage = () => {
    const { user } = useAuth();
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filter, setFilter] = useState('all'); // 'all' or 'unread'
    const [unreadCount, setUnreadCount] = useState(0);

    useEffect(() => {
        fetchNotifications();
    }, [user?.userId]);

    useEffect(() => {
        // Filter notifications based on selected filter
        updateUnreadCount();
        // Set up refresh interval for new notifications
        const interval = setInterval(fetchNotifications, 30000); // Refresh every 30 seconds
        return () => clearInterval(interval);
    }, [notifications]);

    const fetchNotifications = async () => {
        if (!user?.userId) return;
        
        try {
            setLoading(true);
            setError(null);
            const response = await notificationService.getAllNotifications(user.userId);
            console.log('Notifications fetched:', response);
            setNotifications(response);
            
            // Calculer le nombre de notifications non lues
            const unreadCount = response.filter(n => !n.isRead).length;
            setUnreadCount(unreadCount);
        } catch (err) {
            setError('Erreur lors du chargement des notifications');
            console.error('Error fetching notifications:', err);
        } finally {
            setLoading(false);
        }
    };

    const updateUnreadCount = async () => {
        if (!user?.userId) return;
        
        try {
            const count = await notificationService.countUnreadNotifications(user.userId);
            setUnreadCount(count);
        } catch (err) {
            console.error('Error counting unread:', err);
        }
    };

    const handleMarkAsRead = async (notificationId, isRead) => {
        if (isRead) return; // Already read
        
        try {
            console.log('Marking notification as read:', notificationId);
            const response = await notificationService.markAsRead(notificationId);
            console.log('Response:', response);
            
            // Mettre à jour l'état local immédiatement
            const updatedNotifications = notifications.map(n =>
                n.id === notificationId ? { ...n, isRead: true } : n
            );
            setNotifications(updatedNotifications);
            
            // Recalculer le nombre de notifications non lues
            const newUnreadCount = updatedNotifications.filter(n => !n.isRead).length;
            setUnreadCount(newUnreadCount);
        } catch (err) {
            setError('Erreur lors de la mise à jour de la notification');
            console.error('Error marking as read:', err);
        }
    };

    const handleMarkAllAsRead = async () => {
        if (unreadCount === 0) return;
        
        try {
            console.log('Marking all notifications as read for user:', user.userId);
            await notificationService.markAllAsRead(user.userId);
            console.log('All marked as read, refreshing...');
            
            // Rafraîchir les notifications depuis le serveur
            await fetchNotifications();
            setUnreadCount(0);
        } catch (err) {
            setError('Erreur lors de la mise à jour des notifications');
            console.error('Error marking all as read:', err);
        }
    };

    const getNotificationIcon = (type) => {
        switch (type?.toLowerCase()) {
            case 'success':
                return <CheckCircle className="w-5 h-5 text-green-600" />;
            case 'warning':
                return <AlertCircle className="w-5 h-5 text-yellow-600" />;
            case 'error':
                return <AlertCircle className="w-5 h-5 text-red-600" />;
            default:
                return <Info className="w-5 h-5 text-blue-600" />;
        }
    };

    const getNotificationColor = (type) => {
        switch (type?.toLowerCase()) {
            case 'success':
                return 'bg-green-50 border-green-200';
            case 'warning':
                return 'bg-yellow-50 border-yellow-200';
            case 'error':
                return 'bg-red-50 border-red-200';
            default:
                return 'bg-blue-50 border-blue-200';
        }
    };

    const filteredNotifications = filter === 'unread' 
        ? notifications.filter(n => !n.isRead)
        : notifications;

    if (loading) {
        return <LoadingSpinner size="lg" message="Chargement des notifications..." />;
    }

    return (
        <div className="space-y-6 animate-fade-in">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <div className="bg-blue-100 p-4 rounded-2xl relative">
                        <Bell className="w-8 h-8 text-blue-600" />
                        {unreadCount > 0 && (
                            <span className="absolute top-1 right-1 bg-red-500 text-white text-xs font-bold px-2 py-0.5 rounded-full">
                                {unreadCount > 99 ? '99+' : unreadCount}
                            </span>
                        )}
                    </div>
                    <div>
                        <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
                            Notifications
                        </h1>
                        <p className="text-slate-500 mt-1">
                            {unreadCount > 0 ? `${unreadCount} non lue${unreadCount > 1 ? 's' : ''}` : 'Toutes les notifications lues'}
                        </p>
                    </div>
                </div>

                {unreadCount > 0 && (
                    <button
                        onClick={handleMarkAllAsRead}
                        className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-6 rounded-xl transition-all shadow-lg shadow-blue-600/20"
                    >
                        <CheckCheck className="w-5 h-5" />
                        Tout marquer comme lu
                    </button>
                )}
            </div>

            {error && <ErrorAlert message={error} onClose={() => setError(null)} />}

            {/* Filter Buttons */}
            <div className="flex gap-2">
                <button
                    onClick={() => setFilter('all')}
                    className={`px-4 py-2 rounded-lg font-semibold transition-all ${
                        filter === 'all'
                            ? 'bg-blue-600 text-white shadow-lg shadow-blue-600/20'
                            : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                    }`}
                >
                    Toutes ({notifications.length})
                </button>
                <button
                    onClick={() => setFilter('unread')}
                    className={`px-4 py-2 rounded-lg font-semibold transition-all ${
                        filter === 'unread'
                            ? 'bg-blue-600 text-white shadow-lg shadow-blue-600/20'
                            : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                    }`}
                >
                    Non lues ({notifications.filter(n => !n.isRead).length})
                </button>
            </div>

            {/* Notifications List */}
            <div className="space-y-3">
                {filteredNotifications.length > 0 ? (
                    filteredNotifications.map((notification) => (
                        <div
                            key={notification.id}
                            className={`border rounded-2xl p-5 transition-all cursor-pointer hover:shadow-lg ${
                                notification.isRead
                                    ? 'bg-white border-slate-100'
                                    : `${getNotificationColor(notification.type)} border shadow-md`
                            }`}
                            onClick={() => handleMarkAsRead(notification.id, notification.isRead)}
                        >
                            <div className="flex items-start gap-4">
                                {/* Icon */}
                                <div className="pt-0.5">
                                    {getNotificationIcon(notification.type)}
                                </div>

                                {/* Content */}
                                <div className="flex-1 min-w-0">
                                    <div className="flex items-center gap-2 mb-2">
                                        <h3 className="font-bold text-slate-900">
                                            {notification.message}
                                        </h3>
                                        {!notification.isRead && (
                                            <span className="flex-shrink-0 w-2 h-2 bg-blue-600 rounded-full"></span>
                                        )}
                                    </div>

                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center gap-3 text-sm text-slate-500">
                                            {notification.passNumber && (
                                                <span className="bg-slate-100 px-2 py-1 rounded text-xs font-semibold">
                                                    Pass: {notification.passNumber}
                                                </span>
                                            )}
                                            <span className="text-xs">
                                                {new Date(notification.createdAt).toLocaleString('fr-FR', {
                                                    year: 'numeric',
                                                    month: 'numeric',
                                                    day: 'numeric',
                                                    hour: '2-digit',
                                                    minute: '2-digit'
                                                })}
                                            </span>
                                        </div>

                                        <div className="flex-shrink-0">
                                            {notification.isRead ? (
                                                <span className="text-xs text-slate-400 font-semibold">Lu</span>
                                            ) : (
                                                <span className="text-xs bg-blue-600 text-white px-2.5 py-1 rounded-full font-bold">
                                                    Nouveau
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="text-center py-16">
                        <Bell className="w-16 h-16 text-slate-300 mx-auto mb-4" />
                        <p className="text-slate-500 font-semibold text-lg">
                            {filter === 'unread' ? 'Aucune notification non lue' : 'Aucune notification'}
                        </p>
                        <p className="text-slate-400 text-sm mt-2">
                            {filter === 'unread'
                                ? 'Vous êtes à jour!'
                                : 'Vous recevrez des notifications ici'}
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default NotificationsPage;
