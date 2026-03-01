import React, { useState, useEffect } from 'react';
import { api } from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { Bus, Train, RefreshCw, AlertCircle } from 'lucide-react';

const TripsPage = () => {
    const { user } = useAuth();
    const [trips, setTrips] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isSimulating, setIsSimulating] = useState(false);
    const [error, setError] = useState(null);

    // Formulaire Simulation
    const [transportType, setTransportType] = useState('BUS');
    const [distanceKm, setDistanceKm] = useState('');

    const fetchTrips = async () => {
        setIsLoading(true);
        setError(null);


        try {
            const response = await api.get(`/api/trips/user/${user.id}`);
            setTrips(response.data);
        } catch (err) {
            console.error(err);
            setError("Erreur lors de la récupération de l'historique.");
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
            await api.post('/api/trips/process', payload);
            setDistanceKm('');
            fetchTrips(); // Rafraîchir la liste
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || "Le trajet n'a pas pu être validé. Vérifiez votre solde.");
        } finally {
            setIsSimulating(false);
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-bold text-gray-900">Mes Trajets</h1>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

                {/* Colonne Gauche : Formulaire de simulation */}
                <div className="lg:col-span-1 border rounded-2xl bg-white p-6 shadow-sm border-gray-100">
                    <h2 className="text-lg font-bold text-gray-800 mb-4 flex items-center">
                        Simuler un trajet
                    </h2>
                    <form className="space-y-4" onSubmit={simulateTrip}>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Type de transport</label>
                            <select
                                value={transportType}
                                onChange={(e) => setTransportType(e.target.value)}
                                className="w-full border-gray-300 rounded-lg shadow-sm focus:ring-indigo-500 focus:border-indigo-500 p-2.5 bg-gray-50 border transition-colors"
                            >
                                <option value="BUS">Bus Urbain</option>
                                <option value="BRT">BRT (Bus Rapide)</option>
                                <option value="TER">TER (Train Express)</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Distance (km)</label>
                            <input
                                type="number"
                                min="0.1"
                                step="0.1"
                                value={distanceKm}
                                onChange={(e) => setDistanceKm(e.target.value)}
                                className="w-full border-gray-300 rounded-lg shadow-sm focus:ring-indigo-500 focus:border-indigo-500 p-2.5 bg-gray-50 border transition-colors"
                                placeholder="Ex: 5.5"
                                required
                            />
                        </div>

                        {error && (
                            <div className="flex items-start text-red-600 bg-red-50 p-3 rounded-md text-sm">
                                <AlertCircle className="w-5 h-5 mr-2 shrink-0" />
                                <span>{error}</span>
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={isSimulating}
                            className="w-full bg-indigo-600 text-white font-medium py-2.5 rounded-lg hover:bg-indigo-700 transition flex justify-center items-center disabled:opacity-70"
                        >
                            {isSimulating ? <RefreshCw className="w-5 h-5 animate-spin" /> : 'Valider le trajet'}
                        </button>
                    </form>
                </div>

                {/* Colonne Droite : Historique */}
                <div className="lg:col-span-2 bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden flex flex-col items-stretch">
                    <div className="p-6 border-b border-gray-100 flex justify-between items-center">
                        <h2 className="text-lg font-bold text-gray-800">Historique récent</h2>
                        <button onClick={fetchTrips} className="text-indigo-600 hover:text-indigo-800">
                            <RefreshCw className="w-5 h-5" />
                        </button>
                    </div>

                    <div className="p-0 overflow-x-auto flex-1">
                        {isLoading ? (
                            <div className="flex py-12 justify-center text-gray-500">Chargement...</div>
                        ) : trips.length === 0 ? (
                            <div className="flex py-12 justify-center text-gray-500">Aucun trajet enregistré.</div>
                        ) : (
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-50">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Transport</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Distance</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Montant Final</th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
                                    </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-100">
                                    {trips.map(trip => (
                                        <tr key={trip.id} className="hover:bg-gray-50 transition-colors">
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <div className="flex items-center">
                                                    {trip.transportType === 'TER' ? <Train className="w-5 h-5 text-gray-400 mr-2" /> : <Bus className="w-5 h-5 text-gray-400 mr-2" />}
                                                    <span className="text-sm font-medium text-gray-900">{trip.transportType}</span>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {trip.distanceKm} km
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-semibold text-gray-900">
                                                {trip.finalFare} FCFA
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${trip.status === 'PAID' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
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
    );
};

export default TripsPage;
