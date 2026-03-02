import React, { useState, useEffect } from 'react';
import { api } from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { Bus, Train, RefreshCw, AlertCircle, MapPin, Navigation, ArrowRight } from 'lucide-react';

const TripsPage = () => {
    const { user } = useAuth();
    const [trips, setTrips] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isSimulating, setIsSimulating] = useState(false);
    const [error, setError] = useState(null);

    const [transportType, setTransportType] = useState('BUS');
    const [distanceKm, setDistanceKm] = useState('');

    const fetchTrips = async () => {
        setIsLoading(true);
        setError(null);

        try {
            const response = await api.get(`/trip-management/api/trips/user/${user.id}`);
            setTrips(response.data);
        } catch (err) {
            console.error(err);
            setError("Erreur lors de la récupération de l'historique des trajets. Le service est peut-être indisponible.");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (user?.id) fetchTrips();
        // eslint-disable-next-line
    }, [user]);

    const simulateTrip = async (e) => {
        e.preventDefault();
        if (!distanceKm) return;

        setIsSimulating(true);
        setError(null);

        const payload = {
            userId: user.id,
            transportType,
            distanceKm: parseFloat(distanceKm)
        };

        try {
            // Appelle API Gateway -> Trip Management Service (qui lui-même appelle Pricing)
            await api.post('/trip-management/api/trips/process', payload);
            setDistanceKm('');
            setTimeout(() => {
                fetchTrips(); // Rafraîchir la liste après un léger délai pour la consistance
            }, 500);
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || "Le trajet n'a pas pu être validé. Vérifiez votre solde ou la disponibilité du service.");
        } finally {
            setIsSimulating(false);
        }
    };

    return (
        <div className="space-y-8 animate-fade-in">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">Mes Trajets</h1>
                    <p className="text-slate-500 mt-1">Gérez vos déplacements et simulez de nouveaux trajets.</p>
                </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Colonne Gauche : Formulaire de simulation */}
                <div className="lg:col-span-1">
                    <div className="bg-white rounded-3xl p-8 shadow-sm border border-slate-100 hover:shadow-xl transition-all duration-300">
                        <div className="flex items-center space-x-3 mb-6">
                            <div className="bg-indigo-100 p-3 rounded-xl text-indigo-600">
                                <Navigation className="w-6 h-6" />
                            </div>
                            <h2 className="text-xl font-bold text-slate-800">Nouveau Trajet</h2>
                        </div>

                        <form className="space-y-5" onSubmit={simulateTrip}>
                            <div>
                                <label className="block text-sm font-semibold text-slate-700 mb-2">Transport</label>
                                <select
                                    value={transportType}
                                    onChange={(e) => setTransportType(e.target.value)}
                                    className="w-full border border-slate-200 rounded-xl shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 p-3.5 bg-slate-50 transition-all font-medium text-slate-700 appearance-none outline-none"
                                >
                                    <option value="BUS">Bus Urbain</option>
                                    <option value="BRT">BRT (Bus Rapide)</option>
                                    <option value="TER">TER (Train Express)</option>
                                </select>
                            </div>

                            <div>
                                <label className="block text-sm font-semibold text-slate-700 mb-2">Distance</label>
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none">
                                        <MapPin className="h-5 w-5 text-slate-400" />
                                    </div>
                                    <input
                                        type="number"
                                        min="0.1"
                                        step="0.1"
                                        value={distanceKm}
                                        onChange={(e) => setDistanceKm(e.target.value)}
                                        className="w-full border border-slate-200 rounded-xl shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 p-3.5 pl-11 bg-slate-50 transition-all font-medium text-slate-700 outline-none"
                                        placeholder="Ex: 5.5 km"
                                        required
                                    />
                                    <div className="absolute inset-y-0 right-0 pr-4 flex items-center pointer-events-none">
                                        <span className="text-slate-400 font-medium">km</span>
                                    </div>
                                </div>
                            </div>

                            {error && (
                                <div className="flex items-start text-red-600 bg-red-50/80 p-4 rounded-xl text-sm border border-red-100 animate-fade-in">
                                    <AlertCircle className="w-5 h-5 mr-2 shrink-0" />
                                    <span className="font-medium">{error}</span>
                                </div>
                            )}

                            <button
                                type="submit"
                                disabled={isSimulating}
                                className="w-full bg-indigo-600 text-white font-semibold py-3.5 rounded-xl hover:bg-indigo-700 focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transition-all flex justify-center items-center shadow-lg shadow-indigo-600/30 disabled:opacity-70 disabled:cursor-not-allowed transform hover:-translate-y-0.5 mt-2"
                            >
                                {isSimulating ? <RefreshCw className="w-5 h-5 animate-spin" /> : 'Valider'}
                                {!isSimulating && <ArrowRight className="w-4 h-4 ml-2" />}
                            </button>
                        </form>
                    </div>
                </div>

                {/* Colonne Droite : Historique */}
                <div className="lg:col-span-2">
                    <div className="bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden flex flex-col h-full">
                        <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
                            <h2 className="text-lg font-bold text-slate-800">Historique récent</h2>
                            <button onClick={fetchTrips} className="p-2 text-indigo-600 bg-indigo-50 rounded-lg hover:bg-indigo-100 transition-colors">
                                <RefreshCw className={`w-5 h-5 ${isLoading ? 'animate-spin' : ''}`} />
                            </button>
                        </div>

                        <div className="p-0 overflow-x-auto flex-1 bg-white">
                            {isLoading && trips.length === 0 ? (
                                <div className="flex flex-col py-16 justify-center items-center text-slate-400 space-y-4">
                                    <div className="w-10 h-10 border-4 border-indigo-100 border-t-indigo-500 rounded-full animate-spin"></div>
                                    <p className="font-medium">Chargement des trajets...</p>
                                </div>
                            ) : trips.length === 0 ? (
                                <div className="flex flex-col py-16 justify-center items-center text-slate-400">
                                    <Navigation className="w-12 h-12 mb-4 text-slate-300" />
                                    <p className="font-medium">Aucun trajet enregistré.</p>
                                </div>
                            ) : (
                                <table className="min-w-full divide-y divide-slate-100">
                                    <thead className="bg-slate-50">
                                        <tr>
                                            <th className="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">Transport</th>
                                            <th className="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">Distance</th>
                                            <th className="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">Tarif</th>
                                            <th className="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">Statut</th>
                                        </tr>
                                    </thead>
                                    <tbody className="bg-white divide-y divide-slate-50">
                                        {trips.map((trip, idx) => (
                                            <tr key={trip.id} className="hover:bg-slate-50/80 transition-colors animate-fade-in" style={{ animationDelay: `${idx * 50}ms` }}>
                                                <td className="px-6 py-5 whitespace-nowrap">
                                                    <div className="flex items-center">
                                                        <div className={`p-2 rounded-lg mr-3 ${trip.transportType === 'TER' ? 'bg-orange-50 text-orange-600' : 'bg-blue-50 text-blue-600'}`}>
                                                            {trip.transportType === 'TER' ? <Train className="w-5 h-5" /> : <Bus className="w-5 h-5" />}
                                                        </div>
                                                        <span className="text-sm font-bold text-slate-900">{trip.transportType}</span>
                                                    </div>
                                                </td>
                                                <td className="px-6 py-5 whitespace-nowrap text-sm font-medium text-slate-600">
                                                    {trip.distanceKm} km
                                                </td>
                                                <td className="px-6 py-5 whitespace-nowrap text-sm font-extrabold text-slate-900">
                                                    {trip.finalFare} FCFA
                                                </td>
                                                <td className="px-6 py-5 whitespace-nowrap">
                                                    <span className={`px-3 py-1 inline-flex text-xs leading-5 font-bold rounded-full border ${trip.status === 'PAID' ? 'bg-green-50 text-green-700 border-green-200' : 'bg-red-50 text-red-700 border-red-200'}`}>
                                                        {trip.status}
                                                    </span>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TripsPage;
