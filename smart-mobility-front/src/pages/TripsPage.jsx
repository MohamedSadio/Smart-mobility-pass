import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { passService } from '../services/passService';
import tripService from '../services/tripService';
import TripForm from '../components/TripsPage/TripForm';
import TripTable from '../components/TripsPage/TripTable';
import TripsHeader from '../components/TripsPage/TripsHeader';

const TripsPage = () => {
    const { user } = useAuth();
    const [trips, setTrips] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const [passData, setPassData] = useState(null);

    const [transportType, setTransportType] = useState('BUS');
    const [startStation, setStartStation] = useState('');
    const [endStation, setEndStation] = useState('');

    const fetchTrips = async () => {
        setIsLoading(true);
        setError(null);

        try {
            const data = await tripService.getUserTrips(user.userId);
            setTrips(data);
        } catch (err) {
            console.error('Error fetching trips:', err);
            setError("Erreur lors de la récupération de l'historique des trajets. Le service est peut-être indisponible.");
        } finally {
            setIsLoading(false);
        }
    };

    // Fetch pass data and trips on component mount
    useEffect(() => {
        const fetchPassData = async () => {
            if (user?.userId) {
                try {
                    const pass = await passService.getMobilityPass(user.userId);
                    setPassData(pass);
                } catch (err) {
                    console.error("Failed to fetch mobility pass data", err);
                    setError('Impossible de récupérer vos données de pass.');
                }
            }
        };

        if (user?.userId) {
            fetchPassData();
            fetchTrips();
        }
        // eslint-disable-next-line
    }, [user]);

    const handleRegisterTrip = async (e) => {
        e.preventDefault();
        
        if (!passData?.passNumber || !transportType || !startStation || !endStation) {
            setError('Tous les champs sont obligatoires');
            return;
        }

        setIsSubmitting(true);
        setError(null);

        const tripData = {
            passNumber: passData.passNumber,
            transportType,
            startStation,
            endStation
        };

        try {
            console.log('Registering trip with data:', tripData);
            const result = await tripService.registerTrip(tripData);
            console.log('Trip registered successfully:', result);
            
            setStartStation('');
            setEndStation('');
            setError(null);
            
            // Refresh trips list
            setTimeout(() => {
                fetchTrips();
            }, 500);
        } catch (err) {
            console.error('Error registering trip:', err);
            const errorMsg = err.response?.data?.message || 
                           err.response?.data?.error ||
                           err.message ||
                           'Le trajet n\'a pas pu être validé. Vérifiez votre solde ou la disponibilité du service.';
            setError(errorMsg);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="space-y-8 animate-fade-in">
            <TripsHeader />

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <div className="lg:col-span-1">
                    <TripForm 
                        passData={passData}
                        transportType={transportType}
                        setTransportType={setTransportType}
                        startStation={startStation}
                        setStartStation={setStartStation}
                        endStation={endStation}
                        setEndStation={setEndStation}
                        error={error}
                        isSubmitting={isSubmitting}
                        onSubmit={handleRegisterTrip}
                    />
                </div>

                <div className="lg:col-span-2">
                    <TripTable 
                        trips={trips}
                        isLoading={isLoading}
                        onRefresh={fetchTrips}
                    />
                </div>
            </div>
        </div>
    );
};

export default TripsPage;
