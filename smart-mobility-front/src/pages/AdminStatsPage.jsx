import React, { useState, useEffect } from 'react';
import { BarChart3, Users, TrendingUp, Calendar } from 'lucide-react';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorAlert from '../components/ErrorAlert';

const AdminStatsPage = () => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        // Simuler le chargement des données
        setTimeout(() => {
            setLoading(false);
        }, 800);
    }, []);

    const stats = [
        {
            title: 'Utilisateurs actifs',
            value: '245',
            change: '+12%',
            icon: Users,
            color: 'blue',
        },
        {
            title: 'Trajets aujourd\'hui',
            value: '89',
            change: '+5.2%',
            icon: TrendingUp,
            color: 'green',
        },
        {
            title: 'Revenus ce mois',
            value: '15,430€',
            change: '+8.3%',
            icon: BarChart3,
            color: 'purple',
        },
        {
            title: 'Abonnements actifs',
            value: '156',
            change: '+2.1%',
            icon: Calendar,
            color: 'orange',
        },
    ];

    if (loading) {
        return <LoadingSpinner size="lg" message="Chargement des statistiques..." />;
    }

    return (
        <div className="space-y-8 animate-fade-in">
            {/* Header */}
            <div className="flex items-center gap-4">
                <div className="bg-green-100 p-4 rounded-2xl">
                    <BarChart3 className="w-8 h-8 text-green-600" />
                </div>
                <div>
                    <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
                        Statistiques
                    </h1>
                    <p className="text-slate-500 mt-1">
                        Vue d'ensemble des performances de Smart Mobility Pass
                    </p>
                </div>
            </div>

            {error && <ErrorAlert message={error} onClose={() => setError(null)} />}

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {stats.map((stat, index) => {
                    const Icon = stat.icon;
                    const colorClasses = {
                        blue: 'bg-blue-100 text-blue-600',
                        green: 'bg-green-100 text-green-600',
                        purple: 'bg-purple-100 text-purple-600',
                        orange: 'bg-orange-100 text-orange-600',
                    };

                    return (
                        <div
                            key={index}
                            className="bg-white rounded-2xl p-6 border border-slate-100 shadow-sm hover:shadow-lg transition-all"
                        >
                            <div className="flex items-start justify-between mb-4">
                                <div className={`p-3 rounded-xl ${colorClasses[stat.color]}`}>
                                    <Icon className="w-6 h-6" />
                                </div>
                                <span className="text-green-600 text-sm font-bold">{stat.change}</span>
                            </div>
                            <p className="text-slate-600 text-sm font-semibold mb-1">{stat.title}</p>
                            <p className="text-2xl font-extrabold text-slate-900">{stat.value}</p>
                        </div>
                    );
                })}
            </div>

            {/* Charts Section - Placeholder */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="bg-white rounded-3xl p-8 border border-slate-100 shadow-sm">
                    <h3 className="text-xl font-bold text-slate-900 mb-6">Trajets par jour</h3>
                    <div className="h-64 flex items-center justify-center bg-slate-50 rounded-xl">
                        <p className="text-slate-500 text-sm">Graphique en développement</p>
                    </div>
                </div>

                <div className="bg-white rounded-3xl p-8 border border-slate-100 shadow-sm">
                    <h3 className="text-xl font-bold text-slate-900 mb-6">Revenus mensuel</h3>
                    <div className="h-64 flex items-center justify-center bg-slate-50 rounded-xl">
                        <p className="text-slate-500 text-sm">Graphique en développement</p>
                    </div>
                </div>
            </div>

            {/* Activity Table */}
            <div className="bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden">
                <div className="p-8 border-b border-slate-100">
                    <h3 className="text-xl font-bold text-slate-900">Activité récente</h3>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-slate-50 border-b border-slate-100">
                            <tr>
                                <th className="px-8 py-4 text-left text-sm font-bold text-slate-700">Utilisateur</th>
                                <th className="px-8 py-4 text-left text-sm font-bold text-slate-700">Action</th>
                                <th className="px-8 py-4 text-left text-sm font-bold text-slate-700">Date</th>
                                <th className="px-8 py-4 text-left text-sm font-bold text-slate-700">Statut</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            <tr className="hover:bg-slate-50 transition-colors">
                                <td className="px-8 py-4 text-sm text-slate-900 font-semibold">Jean Dupont</td>
                                <td className="px-8 py-4 text-sm text-slate-600">Nouveau trajet</td>
                                <td className="px-8 py-4 text-sm text-slate-600">02/03/2026 14:30</td>
                                <td className="px-8 py-4 text-sm">
                                    <span className="bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-bold">
                                        Complété
                                    </span>
                                </td>
                            </tr>
                            <tr className="hover:bg-slate-50 transition-colors">
                                <td className="px-8 py-4 text-sm text-slate-900 font-semibold">Marie Martin</td>
                                <td className="px-8 py-4 text-sm text-slate-600">Recharge abonnement</td>
                                <td className="px-8 py-4 text-sm text-slate-600">02/03/2026 13:15</td>
                                <td className="px-8 py-4 text-sm">
                                    <span className="bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-bold">
                                        Complété
                                    </span>
                                </td>
                            </tr>
                            <tr className="hover:bg-slate-50 transition-colors">
                                <td className="px-8 py-4 text-sm text-slate-900 font-semibold">Pierre Bernard</td>
                                <td className="px-8 py-4 text-sm text-slate-600">Nouveau compte</td>
                                <td className="px-8 py-4 text-sm text-slate-600">02/03/2026 10:45</td>
                                <td className="px-8 py-4 text-sm">
                                    <span className="bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-bold">
                                        Complété
                                    </span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default AdminStatsPage;
