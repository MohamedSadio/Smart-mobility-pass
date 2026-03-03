import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { passService } from '../services/passService';
import { CreditCard, CheckCircle, AlertCircle, ArrowRight, Wallet, BarChart3 } from 'lucide-react';

const BillingPage = () => {
    const { user } = useAuth();
    const [amount, setAmount] = useState('');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });
    const [passData, setPassData] = useState(null);

    useEffect(() => {
        const fetchPassData = async () => {
            if (user?.userId) {
                try {
                    const pass = await passService.getMobilityPass(user.userId);
                    setPassData(pass);
                } catch (err) {
                    console.error("Failed to fetch mobility pass data", err);
                    setMessage({ type: 'error', text: 'Impossible de récupérer vos données de pass.' });
                }
            }
        };
        fetchPassData();
    }, [user]);

    const handleRecharge = async (e) => {
        e.preventDefault();
        if (!amount || amount <= 0) return;
        if (!passData || !passData.passNumber) {
            setMessage({ type: 'error', text: "Erreur : Impossible de récupérer votre numéro de Pass." });
            return;
        }

        setLoading(true);
        setMessage({ type: '', text: '' });

        try {
            await passService.rechargePass(passData.passNumber, amount);
            
            setMessage({ type: 'success', text: `Votre compte a été rechargé de ${amount} FCFA avec succès !` });
            setAmount('');
            
            // Refresh pass data after recharge
            const updatedPass = await passService.getMobilityPass(user.userId);
            setPassData(updatedPass);
        } catch (err) {
            console.error(err);
            setMessage({ type: 'error', text: "Erreur lors du rechargement. Le service est peut-être indisponible." });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-4xl mx-auto space-y-8 pt-4 animate-fade-in">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">Rechargement</h1>
                    <p className="text-slate-500 mt-1">Créditez votre Smart Mobility Pass en toute simplicité.</p>
                </div>
            </div>

            {/* Solde Card */}
            {passData && (
                <div className="bg-gradient-to-br from-indigo-600 to-blue-600 p-8 rounded-3xl shadow-xl text-white relative overflow-hidden">
                    {/* Decorative elements */}
                    <div className="absolute top-[-20%] right-[-10%] w-96 h-96 bg-white/10 rounded-full blur-3xl opacity-50"></div>
                    
                    <div className="relative z-10">
                        <div className="flex items-center justify-between mb-6">
                            <div className="flex items-center space-x-3">
                                <div className="bg-white/20 p-3 rounded-xl">
                                    <BarChart3 className="w-6 h-6" />
                                </div>
                                <div>
                                    <p className="text-white/80 text-sm">Solde actuel</p>
                                    <h3 className="text-4xl font-bold">{passData.balance?.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) || '0.00'} FCFA</h3>
                                </div>
                            </div>
                            <div className="text-right">
                                <p className="text-white/80 text-sm mb-1">Statut</p>
                                <span className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${passData.status === 'ACTIVE' ? 'bg-green-400/20 text-green-200' : 'bg-red-400/20 text-red-200'}`}>
                                    {passData.status}
                                </span>
                            </div>
                        </div>
                        
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 pt-6 border-t border-white/20">
                            <div>
                                <p className="text-white/70 text-xs uppercase tracking-wide mb-1">Pass Number</p>
                                <p className="text-sm font-mono text-white">{passData.passNumber}</p>
                            </div>
                            <div>
                                <p className="text-white/70 text-xs uppercase tracking-wide mb-1">Points Loyauté</p>
                                <p className="text-sm font-bold text-white">{passData.loyaltyPoints || 0}</p>
                            </div>
                            <div>
                                <p className="text-white/70 text-xs uppercase tracking-wide mb-1">Abonnement</p>
                                <p className="text-sm font-bold text-white">{passData.subscriptionType || 'AUCUN'}</p>
                            </div>
                            <div>
                                <p className="text-white/70 text-xs uppercase tracking-wide mb-1">Créé le</p>
                                <p className="text-sm font-bold text-white">{new Date(passData.createdAt).toLocaleDateString('fr-FR')}</p>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            <div className="bg-white p-8 md:p-12 rounded-3xl shadow-xl shadow-slate-200/40 border border-slate-100 flex flex-col md:flex-row gap-12 items-center relative overflow-hidden">
                {/* Decorative background element */}
                <div className="absolute top-[-20%] right-[-10%] w-[500px] h-[500px] bg-gradient-to-br from-indigo-50 to-blue-50 rounded-full blur-3xl opacity-70 pointer-events-none z-0"></div>

                <div className="flex-1 w-full relative z-10">
                    <div className="flex items-center space-x-5 mb-8">
                        <div className="bg-indigo-600 p-4 rounded-2xl shadow-lg shadow-indigo-600/30">
                            <CreditCard className="w-8 h-8 text-white" />
                        </div>
                        <div>
                            <h2 className="text-2xl font-bold text-slate-800">Montant de la recharge</h2>
                            <p className="text-slate-500 text-sm mt-1">Sélectionnez ou saisissez le montant (FCFA).</p>
                        </div>
                    </div>

                    {message.text && (
                        <div className={`mb-8 p-4 rounded-2xl flex items-center border animate-slide-up ${message.type === 'success' ? 'bg-green-50/80 border-green-200 text-green-700' : 'bg-red-50/80 border-red-200 text-red-700'}`}>
                            {message.type === 'success' ? <CheckCircle className="w-6 h-6 mr-3 shrink-0" /> : <AlertCircle className="w-6 h-6 mr-3 shrink-0" />}
                            <span className="font-medium">{message.text}</span>
                        </div>
                    )}

                    <form onSubmit={handleRecharge} className="space-y-6">
                        <div>
                            <div className="relative rounded-2xl shadow-sm">
                                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                    <Wallet className="h-6 w-6 text-slate-400" />
                                </div>
                                <input
                                    type="number"
                                    min="100"
                                    step="100"
                                    required
                                    className="focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 block w-full pl-14 pr-16 text-2xl font-bold py-5 border-slate-200 rounded-2xl border bg-slate-50 transition-all outline-none"
                                    placeholder="0"
                                    value={amount}
                                    onChange={(e) => setAmount(e.target.value)}
                                />
                                <div className="absolute inset-y-0 right-0 pr-5 flex items-center pointer-events-none">
                                    <span className="text-slate-400 text-lg font-bold">FCFA</span>
                                </div>
                            </div>
                        </div>

                        <div className="grid grid-cols-3 gap-3 pt-2">
                            {[1000, 2000, 5000].map(val => (
                                <button
                                    type="button"
                                    key={val}
                                    onClick={() => setAmount(val)}
                                    className={`py-3 rounded-xl font-bold border transition-all ${amount === String(val) ? 'bg-indigo-600 text-white border-indigo-600 shadow-md shadow-indigo-600/20' : 'bg-white text-slate-700 border-slate-200 hover:border-indigo-300 hover:bg-indigo-50'}`}
                                >
                                    {val}
                                </button>
                            ))}
                        </div>

                        <button
                            type="submit"
                            disabled={loading || !amount}
                            className="w-full flex justify-center items-center py-4 border border-transparent text-lg font-bold rounded-xl text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all disabled:opacity-70 disabled:cursor-not-allowed mt-8 shadow-xl shadow-indigo-600/30 hover:-translate-y-1"
                        >
                            {loading ? (
                                <div className="flex items-center">
                                    <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin mr-3"></div>
                                    Traitement sécurisé...
                                </div>
                            ) : (
                                <>
                                    Confirmer le paiement <ArrowRight className="ml-2 w-5 h-5" />
                                </>
                            )}
                        </button>
                    </form>
                </div>

                {/* Illustration Panel */}
                <div className="hidden md:flex flex-col flex-1 items-center justify-center p-8 bg-slate-50/80 rounded-3xl border border-slate-100 z-10 w-full h-full text-center">
                    <img src="https://ui-avatars.com/api/?name=Pass&background=4f46e5&color=fff&rounded=true&size=128" alt="Pass Illustration" className="w-32 h-32 mb-6 shadow-xl rounded-full animate-slide-up" />
                    <h3 className="text-xl font-bold text-slate-800 mb-2">Paiement Simulé</h3>
                    <p className="text-slate-500 text-sm">Le crédit sera instantanément ajouté à votre compte Mobility Pass dans cet environnement de test.</p>
                </div>
            </div>
        </div>
    );
};

export default BillingPage;
