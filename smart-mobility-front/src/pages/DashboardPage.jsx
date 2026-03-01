import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/axios';
import { CreditCard, Activity, ArrowRight, Wallet } from 'lucide-react';
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
                // Appelle API Gateway -> User Mobility Pass Service
                const response = await api.get(`/api/passes/user/${user.id}`);
                setPassData(response.data);
            } catch (err) {
                console.error("Failed to fetch pass", err);
                setError("Impossible de charger les détails du pass. Vérifiez que le service est en ligne.");
            } finally {
                setLoading(false);
            }
        };

        fetchMobilityPass();
    }, [user]);

    if (loading) {
        return <div className="text-gray-500 flex justify-center items-center h-64">Chargement des données...</div>;
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-bold text-gray-900">Vue d'ensemble</h1>
            </div>

            {error ? (
                <div className="bg-red-50 border-l-4 border-red-500 p-4 rounded-md">
                    <p className="text-red-700">{error}</p>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {/* Card: Solde */}
                    <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 flex flex-col justify-between hover:shadow-md transition-shadow">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Solde Actuel</p>
                                <h3 className="text-3xl font-bold text-gray-900 mt-1">
                                    {passData?.balance != null ? `${passData.balance} FCFA` : '---'}
                                </h3>
                            </div>
                            <div className="bg-green-100 p-3 rounded-full">
                                <Wallet className="w-6 h-6 text-green-600" />
                            </div>
                        </div>
                        <div className="mt-4">
                            <Link to="/billing" className="text-sm text-indigo-600 font-medium hover:text-indigo-800 flex items-center">
                                Recharger mon compte <ArrowRight className="w-4 h-4 ml-1" />
                            </Link>
                        </div>
                    </div>

                    {/* Card: Status du Pass */}
                    <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 flex flex-col justify-between hover:shadow-md transition-shadow">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Statut du Pass</p>
                                <div className="flex items-center mt-1 space-x-2">
                                    <h3 className="text-xl font-bold text-gray-900">
                                        {passData?.status || 'Inconnu'}
                                    </h3>
                                    {passData?.status === 'ACTIVE' && (
                                        <span className="flex w-3 h-3 bg-green-500 rounded-full"></span>
                                    )}
                                </div>
                                <p className="text-sm text-gray-500 mt-1 font-mono">{passData?.passNumber}</p>
                            </div>
                            <div className="bg-indigo-100 p-3 rounded-full">
                                <CreditCard className="w-6 h-6 text-indigo-600" />
                            </div>
                        </div>
                    </div>

                    {/* Card: Type d'Abonnement */}
                    <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 flex flex-col justify-between hover:shadow-md transition-shadow">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-gray-500">Abonnement</p>
                                <h3 className="text-xl font-bold text-gray-900 mt-1">
                                    {passData?.subscriptionType || 'STANDARD'}
                                </h3>
                                <p className="text-sm text-gray-500 mt-1">Points Fidélité : <span className="font-semibold text-indigo-600">{passData?.loyaltyPoints || 0}</span></p>
                            </div>
                            <div className="bg-yellow-100 p-3 rounded-full">
                                <Activity className="w-6 h-6 text-yellow-600" />
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Raccourcis / Quick Actions */}
            <div className="mt-8 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
                <h2 className="text-lg font-bold text-gray-900 mb-4">Actions Rapides</h2>
                <div className="flex space-x-4">
                    <Link to="/trips" className="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition">
                        Simuler un Trajet
                    </Link>
                </div>
            </div>

        </div>
    );
};

export default DashboardPage;
