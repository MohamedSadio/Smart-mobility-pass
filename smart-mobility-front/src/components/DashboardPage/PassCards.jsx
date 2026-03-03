import React from 'react';
import { Wallet, CreditCard, Activity, ArrowRight, BadgeCheck } from 'lucide-react';
import { Link } from 'react-router-dom';

/**
 * Balance card for dashboard
 */
export const BalanceCard = ({ balance }) => (
    <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 flex flex-col justify-between hover:shadow-xl transition-all duration-300 group">
        <div className="flex items-center justify-between">
            <div>
                <p className="text-sm font-medium text-slate-500 uppercase tracking-wide">Solde Actuel</p>
                <h3 className="text-4xl font-extrabold text-slate-900 mt-2 tracking-tight group-hover:text-indigo-600 transition-colors">
                    {balance != null ? `${balance} FCFA` : '---'}
                </h3>
            </div>
            <div className="bg-green-100 p-4 rounded-2xl group-hover:scale-110 transition-transform">
                <Wallet className="w-7 h-7 text-green-600" />
            </div>
        </div>
        <div className="mt-6 pt-4 border-t border-slate-100">
            <Link to="/billing" className="text-sm font-semibold text-indigo-600 flex items-center hover:text-indigo-800 transition-colors">
                Recharger mon compte <ArrowRight className="w-4 h-4 ml-1.5 group-hover:translate-x-1 transition-transform" />
            </Link>
        </div>
    </div>
);

/**
 * Pass status card for dashboard
 */
export const PassStatusCard = ({ status, passNumber }) => (
    <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 flex flex-col justify-between hover:shadow-xl transition-all duration-300 group relative overflow-hidden">
        <div className="absolute -right-6 -top-6 w-24 h-24 bg-indigo-50 rounded-full group-hover:scale-150 transition-transform duration-500 ease-out z-0"></div>
        <div className="relative z-10 flex items-center justify-between">
            <div>
                <p className="text-sm font-medium text-slate-500 uppercase tracking-wide">Statut du Pass</p>
                <div className="flex items-center mt-2 space-x-2">
                    <h3 className="text-2xl font-bold text-slate-900">{status || 'Inconnu'}</h3>
                    {status === 'ACTIVE' && <BadgeCheck className="w-6 h-6 text-green-500" />}
                </div>
                <p className="text-sm text-slate-400 mt-1 font-mono tracking-wider">{passNumber}</p>
            </div>
            <div className="bg-indigo-100 p-4 rounded-2xl group-hover:scale-110 transition-transform">
                <CreditCard className="w-7 h-7 text-indigo-600" />
            </div>
        </div>
    </div>
);

/**
 * Subscription type card for dashboard
 */
export const SubscriptionCard = ({ subscriptionType, loyaltyPoints }) => (
    <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 flex flex-col justify-between hover:shadow-xl transition-all duration-300 group">
        <div className="flex items-center justify-between">
            <div>
                <p className="text-sm font-medium text-slate-500 uppercase tracking-wide">Abonnement</p>
                <h3 className="text-2xl font-bold text-slate-900 mt-2">
                    {subscriptionType || 'STANDARD'}
                </h3>
                <div className="mt-2 inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold bg-blue-50 text-blue-700">
                    Points Fidélité: <span className="ml-1 text-blue-900">{loyaltyPoints || 0}</span>
                </div>
            </div>
            <div className="bg-yellow-100 p-4 rounded-2xl group-hover:scale-110 transition-transform">
                <Activity className="w-7 h-7 text-yellow-600" />
            </div>
        </div>
    </div>
);

/**
 * Pass cards section
 */
export const PassCards = ({ passData, error }) => {
    if (error) {
        return (
            <div className="bg-red-50/80 backdrop-blur-sm border border-red-200 text-red-600 p-5 rounded-2xl shadow-sm">
                <p className="font-medium">{error}</p>
            </div>
        );
    }

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <BalanceCard balance={passData?.balance} />
            <PassStatusCard status={passData?.status} passNumber={passData?.passNumber} />
            <SubscriptionCard subscriptionType={passData?.subscriptionType} loyaltyPoints={passData?.loyaltyPoints} />
        </div>
    );
};
