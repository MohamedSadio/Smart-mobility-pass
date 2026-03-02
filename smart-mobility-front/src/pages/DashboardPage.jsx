import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/axios';
import { CreditCard, Activity, ArrowRight, Wallet, BadgeCheck } from 'lucide-react';
import { Link } from 'react-router-dom';

const DashboardPage = () => {
    const { user } = useAuth();
    const [passData, setPassData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchMobilityPass = async () => {
            if (!user?.id) return;

            try {
                // BUG FIX: Appelle API Gateway -> User Mobility Pass Service via the correct endpoint
                const response = await api.get(`/user-mobility-pass/api/mobility-passes/user/${user.id}`);
                setPassData(response.data);
            } catch (err) {
                console.error("Failed to fetch pass", err);
                setError("Impossible de charger les détails du pass. Le service est peut-être indisponible ou vous n'avez pas encore de pass.");
            } finally {
                setLoading(false);
            }
        };

        fetchMobilityPass();
    }, [user]);

    if (loading) {
        return (
            <div className="flex justify-center items-center h-64 animate-fade-in">
                <div className="w-12 h-12 border-4 border-indigo-200 border-t-indigo-600 rounded-full animate-spin"></div>
            </div>
        );
    }

    return (
        <div className="space-y-8 animate-fade-in">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">Bonjour, {user?.firstName} 👋</h1>
                    <p className="text-slate-500 mt-1">Voici la vue d'ensemble de votre compte Smart Mobility Pass.</p>
                </div>
                <Link to="/trips" className="inline-flex items-center justify-center px-5 py-2.5 bg-indigo-600 text-white text-sm font-semibold rounded-xl hover:bg-indigo-700 shadow-lg shadow-indigo-600/30 transition-all hover:-translate-y-0.5">
                    Simuler un trajet <ArrowRight className="ml-2 w-4 h-4" />
                </Link>
            </div>

            {error ? (
                <div className="bg-red-50/80 backdrop-blur-sm border border-red-200 text-red-600 p-5 rounded-2xl shadow-sm">
                    <p className="font-medium">{error}</p>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {/* Card: Solde */}
                    <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 flex flex-col justify-between hover:shadow-xl transition-all duration-300 group">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-slate-500 uppercase tracking-wide">Solde Actuel</p>
                                <h3 className="text-4xl font-extrabold text-slate-900 mt-2 tracking-tight group-hover:text-indigo-600 transition-colors">
                                    {passData?.balance != null ? `${passData.balance} FCFA` : '---'}
                                </h3>
                            </div>
                            <div className="bg-green-100 p-4 rounded-2xl group-hover:scale-110 transition-transform">
                                <Wallet className="w-7 h-7 text-green-600" />
                            </div>
                        </div>
                        <div className="mt-6 pt-4 border-t border-slate-100">
                            <Link to="/billing" className="text-sm font-semibold text-indigo-600 flex items-center hover:text-indigo-800 transition-colors">
                                Recharger mon compte <ArrowRight className="w-4 h-4 ml-1.5 group-hover:translate-x-1 transition-transform" />
                            </Link>
                        </div>
                    </div>

                    {/* Card: Status du Pass */}
                    <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 flex flex-col justify-between hover:shadow-xl transition-all duration-300 group relative overflow-hidden">
                        <div className="absolute -right-6 -top-6 w-24 h-24 bg-indigo-50 rounded-full group-hover:scale-150 transition-transform duration-500 ease-out z-0"></div>
                        <div className="relative z-10 flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-slate-500 uppercase tracking-wide">Statut du Pass</p>
                                <div className="flex items-center mt-2 space-x-2">
                                    <h3 className="text-2xl font-bold text-slate-900">
                                        {passData?.status || 'Inconnu'}
                                    </h3>
                                    {passData?.status === 'ACTIVE' && (
                                        <BadgeCheck className="w-6 h-6 text-green-500" />
                                    )}
                                </div>
                                <p className="text-sm text-slate-400 mt-1 font-mono tracking-wider">{passData?.passNumber}</p>
                            </div>
                            <div className="bg-indigo-100 p-4 rounded-2xl group-hover:scale-110 transition-transform">
                                <CreditCard className="w-7 h-7 text-indigo-600" />
                            </div>
                        </div>
                    </div>

                    {/* Card: Type d'Abonnement */}
                    <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 flex flex-col justify-between hover:shadow-xl transition-all duration-300 group">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-slate-500 uppercase tracking-wide">Abonnement</p>
                                <h3 className="text-2xl font-bold text-slate-900 mt-2">
                                    {passData?.subscriptionType || 'STANDARD'}
                                </h3>
                                <div className="mt-2 inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold bg-blue-50 text-blue-700">
                                    Points Fidélité : <span className="ml-1 text-blue-900">{passData?.loyaltyPoints || 0}</span>
                                </div>
                            </div>
                            <div className="bg-yellow-100 p-4 rounded-2xl group-hover:scale-110 transition-transform">
                                <Activity className="w-7 h-7 text-yellow-600" />
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default DashboardPage;
