import React from 'react';
import { Navigation, MapPin, RefreshCw, ArrowRight, AlertCircle, Ticket } from 'lucide-react';

const TripForm = ({ 
    passData,
    transportType, 
    setTransportType, 
    startStation,
    setStartStation,
    endStation,
    setEndStation,
    error, 
    isSubmitting, 
    onSubmit 
}) => {
    const transportOptions = [
        { label: 'Bus Urbain', value: 'BUS' },
        { label: 'BRT (Bus Rapide)', value: 'BRT' },
        { label: 'TER (Train Express)', value: 'TER' }
    ];

    const commonStations = [
        'Centre Ville', 'Gare Centrale', 'Aéroport', 'Port',
        'Université', 'Hôpital Central', 'Marché Principal', 'Parc Zoologique'
    ];

    return (
        <div className="bg-white rounded-3xl p-8 shadow-sm border border-slate-100 hover:shadow-xl transition-all duration-300">
            <div className="flex items-center space-x-3 mb-6">
                <div className="bg-indigo-100 p-3 rounded-xl text-indigo-600">
                    <Navigation className="w-6 h-6" />
                </div>
                <h2 className="text-xl font-bold text-slate-800">Nouveau Trajet</h2>
            </div>

            <form className="space-y-5" onSubmit={onSubmit}>

                {/* Pass Number — lecture seule, chargé automatiquement */}
                <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-2">
                        <Ticket className="w-4 h-4 inline mr-1" />
                        Numéro de Pass
                    </label>
                    {passData?.passNumber ? (
                        <div className="w-full border border-indigo-200 rounded-xl p-3.5 bg-indigo-50 font-medium text-indigo-700 flex items-center justify-between">
                            <span>{passData.passNumber}</span>
                            <span className={`text-xs px-2 py-1 rounded-full font-semibold ${
                                passData.status === 'ACTIVE' 
                                    ? 'bg-green-100 text-green-700' 
                                    : 'bg-red-100 text-red-700'
                            }`}>
                                {passData.status}
                            </span>
                        </div>
                    ) : (
                        <div className="w-full border border-slate-200 rounded-xl p-3.5 bg-slate-50 text-slate-400 text-sm italic">
                            Chargement du pass...
                        </div>
                    )}
                </div>

                {/* Solde disponible */}
                {passData?.balance !== undefined && (
                    <div className="bg-slate-50 rounded-xl p-3.5 border border-slate-200 flex justify-between items-center">
                        <span className="text-sm font-semibold text-slate-600">Solde disponible</span>
                        <span className="text-lg font-bold text-indigo-600">
                            {passData.balance?.toLocaleString('fr-FR')} FCFA
                        </span>
                    </div>
                )}

                {/* Transport Type */}
                <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-2">
                        Type de Transport
                    </label>
                    <select
                        value={transportType}
                        onChange={(e) => setTransportType(e.target.value)}
                        className="w-full border border-slate-200 rounded-xl shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 p-3.5 bg-slate-50 transition-all font-medium text-slate-700 appearance-none outline-none"
                    >
                        {transportOptions.map(option => (
                            <option key={option.value} value={option.value}>
                                {option.label}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Start Station */}
                <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-2">
                        <MapPin className="w-4 h-4 inline mr-1" />
                        Station de Départ
                    </label>
                    <input
                        type="text"
                        list="stationList"
                        value={startStation}
                        onChange={(e) => setStartStation(e.target.value)}
                        placeholder="Ex: Centre Ville"
                        className="w-full border border-slate-200 rounded-xl shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 p-3.5 bg-slate-50 transition-all font-medium text-slate-700 outline-none"
                        required
                    />
                    <datalist id="stationList">
                        {commonStations.map(station => (
                            <option key={station} value={station} />
                        ))}
                    </datalist>
                </div>

                {/* End Station */}
                <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-2">
                        <MapPin className="w-4 h-4 inline mr-1" />
                        Station d'Arrivée
                    </label>
                    <input
                        type="text"
                        list="stationList"
                        value={endStation}
                        onChange={(e) => setEndStation(e.target.value)}
                        placeholder="Ex: Aéroport"
                        className="w-full border border-slate-200 rounded-xl shadow-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 p-3.5 bg-slate-50 transition-all font-medium text-slate-700 outline-none"
                        required
                    />
                </div>

                {error && (
                    <div className="flex items-start text-red-600 bg-red-50/80 p-4 rounded-xl text-sm border border-red-100">
                        <AlertCircle className="w-5 h-5 mr-2 shrink-0 mt-0.5" />
                        <span className="font-medium">{error}</span>
                    </div>
                )}

                <button
                    type="submit"
                    disabled={isSubmitting || !passData?.passNumber || passData?.status !== 'ACTIVE'}
                    className="w-full bg-indigo-600 text-white font-semibold py-3.5 rounded-xl hover:bg-indigo-700 focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transition-all flex justify-center items-center shadow-lg shadow-indigo-600/30 disabled:opacity-70 disabled:cursor-not-allowed transform hover:-translate-y-0.5 mt-2"
                >
                    {isSubmitting ? (
                        <RefreshCw className="w-5 h-5 animate-spin" />
                    ) : (
                        <>
                            Valider le Trajet
                            <ArrowRight className="w-4 h-4 ml-2" />
                        </>
                    )}
                </button>
            </form>
        </div>
    );
};

export default TripForm;