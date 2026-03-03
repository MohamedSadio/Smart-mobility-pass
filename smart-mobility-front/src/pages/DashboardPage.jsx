import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import passService from '../services/passService';
import LoadingSpinner from '../components/LoadingSpinner';
import DashboardHeader from '../components/DashboardPage/DashboardHeader';
import { PassCards } from '../components/DashboardPage/PassCards';

const DashboardPage = () => {
    const { user } = useAuth();
    const [passData, setPassData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchMobilityPass = async () => {
            if (!user?.id) return;

            try {
                const data = await passService.getMobilityPass(user.id);
                setPassData(data);
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
                <LoadingSpinner size="md" />
            </div>
        );
    }

    return (
        <div className="space-y-8 animate-fade-in">
            <DashboardHeader firstName={user?.firstName} />
            <PassCards passData={passData} error={error} />
        </div>
    );
};

export default DashboardPage;
