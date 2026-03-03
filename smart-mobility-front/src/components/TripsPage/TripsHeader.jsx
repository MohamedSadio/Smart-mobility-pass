import React from 'react';

/**
 * Trips page header
 */
const TripsHeader = () => {
    return (
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
                <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
                    Mes Trajets
                </h1>
                <p className="text-slate-500 mt-1">
                    Gérez vos déplacements et simulez de nouveaux trajets.
                </p>
            </div>
        </div>
    );
};

export default TripsHeader;
