import React from 'react';

/**
 * Error message display component
 */
const ErrorAlert = ({ error }) => {
    if (!error) return null;

    return (
        <div className="bg-red-50/80 backdrop-blur-sm border border-red-200 text-red-600 p-4 rounded-xl text-sm text-center shadow-sm animate-fade-in">
            {error}
        </div>
    );
};

export default ErrorAlert;
