import React from 'react';

/**
 * Reusable loading spinner component
 */
const LoadingSpinner = ({ size = 'md', message = null }) => {
    const sizeClasses = {
        sm: 'w-8 h-8 border-2',
        md: 'w-12 h-12 border-4',
        lg: 'w-16 h-16 border-4'
    };

    return (
        <div className="flex flex-col justify-center items-center gap-3">
            <div className={`${sizeClasses[size]} border-indigo-200 border-t-indigo-600 rounded-full animate-spin`}></div>
            {message && <p className="text-slate-500 font-medium text-sm">{message}</p>}
        </div>
    );
};

export default LoadingSpinner;
