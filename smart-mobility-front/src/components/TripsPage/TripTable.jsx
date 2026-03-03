import React from 'react';
import { Bus, Train, Navigation, RefreshCw } from 'lucide-react';
import LoadingSpinner from '../LoadingSpinner';

/**
 * Trip table row component
 */
const TripRow = ({ trip, index }) => {
    const isTrainTransport = trip.transportType === 'TER';
    const isSuccess = trip.status === 'SUCCESS';

    return (
        <tr 
            key={trip.tripId} 
            className="hover:bg-slate-50/80 transition-colors animate-fade-in" 
            style={{ animationDelay: `${index * 50}ms` }}
        >
            <td className="px-6 py-5 whitespace-nowrap">
                <div className="flex items-center">
                    <div className={`p-2 rounded-lg mr-3 ${isTrainTransport ? 'bg-orange-50 text-orange-600' : 'bg-blue-50 text-blue-600'}`}>
                        {isTrainTransport ? (
                            <Train className="w-5 h-5" />
                        ) : (
                            <Bus className="w-5 h-5" />
                        )}
                    </div>
                    <span className="text-sm font-bold text-slate-900">
                        {trip.transportType}
                    </span>
                </div>
            </td>
            <td className="px-6 py-5 whitespace-nowrap text-sm text-slate-600">
                <div>
                    <p className="font-medium">{trip.startStation}</p>
                    <p className="text-xs text-slate-400">→ {trip.endStation}</p>
                </div>
            </td>
            <td className="px-6 py-5 whitespace-nowrap text-sm font-medium text-slate-600">
                {trip.distanceKm?.toFixed(2) || '0.00'} km
            </td>
            <td className="px-6 py-5 whitespace-nowrap text-sm font-extrabold text-slate-900">
                {parseFloat(trip.finalFare || 0).toFixed(2)} FCFA
            </td>
            <td className="px-6 py-5 whitespace-nowrap">
                <span className={`px-3 py-1 inline-flex text-xs leading-5 font-bold rounded-full border ${
                    isSuccess 
                        ? 'bg-green-50 text-green-700 border-green-200' 
                        : 'bg-red-50 text-red-700 border-red-200'
                }`}>
                    {trip.status}
                </span>
            </td>
            <td className="px-6 py-5 whitespace-nowrap text-xs text-slate-400">
                {new Date(trip.createdAt).toLocaleDateString('fr-FR')}
            </td>
        </tr>
    );
};

/**
 * Trips table with history
 */
const TripTable = ({ trips, isLoading, onRefresh }) => {
    const isEmpty = !isLoading && trips.length === 0;

    return (
        <div className="bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden flex flex-col h-full">
            <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
                <h2 className="text-lg font-bold text-slate-800">Historique récent</h2>
                <button 
                    onClick={onRefresh} 
                    className="p-2 text-indigo-600 bg-indigo-50 rounded-lg hover:bg-indigo-100 transition-colors"
                >
                    <RefreshCw className={`w-5 h-5 ${isLoading ? 'animate-spin' : ''}`} />
                </button>
            </div>

            <div className="p-0 overflow-x-auto flex-1 bg-white">
                {isLoading && trips.length === 0 ? (
                    <div className="flex flex-col py-16 justify-center items-center text-slate-400">
                        <LoadingSpinner size="md" message="Chargement des trajets..." />
                    </div>
                ) : isEmpty ? (
                    <div className="flex flex-col py-16 justify-center items-center text-slate-400">
                        <Navigation className="w-12 h-12 mb-4 text-slate-300" />
                        <p className="font-medium">Aucun trajet enregistré.</p>
                    </div>
                ) : (
                    <table className="min-w-full divide-y divide-slate-100">
                        <thead className="bg-slate-50">
                            <tr>
                                <th className="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">
                                    Transport
                                </th>
                                <th className="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">
                                    Trajet
                                </th>
                                <th className="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">
                                    Distance
                                </th>
                                <th className="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">
                                    Tarif
                                </th>
                                <th className="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">
                                    Statut
                                </th>
                                <th className="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">
                                    Date
                                </th>
                            </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-slate-50">
                            {trips.map((trip, idx) => (
                                <TripRow key={trip.tripId} trip={trip} index={idx} />
                            ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

export default TripTable;
