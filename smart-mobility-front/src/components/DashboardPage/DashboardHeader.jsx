import React from 'react';
import { ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

/**
 * Dashboard page header with greeting and CTA
 */
const DashboardHeader = ({ firstName }) => {
    return (
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
                <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
                    Bonjour, {firstName} 👋
                </h1>
                <p className="text-slate-500 mt-1">
                    Voici la vue d'ensemble de votre compte Smart Mobility Pass.
                </p>
            </div>
            <Link 
                to="/trips" 
                className="inline-flex items-center justify-center px-5 py-2.5 bg-indigo-600 text-white text-sm font-semibold rounded-xl hover:bg-indigo-700 shadow-lg shadow-indigo-600/30 transition-all hover:-translate-y-0.5"
            >
                Simuler un trajet <ArrowRight className="ml-2 w-4 h-4" />
            </Link>
        </div>
    );
};

export default DashboardHeader;
