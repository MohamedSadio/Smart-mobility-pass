import React from 'react';

/**
 * Login page header with branding
 */
const LoginHeader = ({ isLogin }) => {
    return (
        <div className="text-center">
            <div className="mx-auto w-16 h-16 flex items-center justify-center shadow-lg transform transition hover:scale-105">
                <img src="/logo.svg" alt="Smart Mobility Logo" className="w-full h-full object-contain" />
            </div>
            <h2 className="mt-6 text-3xl font-extrabold text-slate-800 tracking-tight">
                Smart Mobility Pass
            </h2>
            <p className="mt-2 text-sm text-slate-500">
                {isLogin 
                    ? 'Connectez-vous pour gérer vos trajets' 
                    : 'Créez votre compte pour commencer'
                }
            </p>
        </div>
    );
};

export default LoginHeader;
