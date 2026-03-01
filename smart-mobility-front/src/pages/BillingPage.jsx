import React, { useState } from 'react';
import { api } from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { CreditCard, CheckCircle, AlertCircle } from 'lucide-react';

const BillingPage = () => {
    const { user } = useAuth();
    const [amount, setAmount] = useState('');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });

    const handleRecharge = async (e) => {
        e.preventDefault();
        if (!amount || amount <= 0) return;

        setLoading(true);
        setMessage({ type: '', text: '' });



        try {
            await api.post('/api/billing/recharge', {
                userId: user.id,
                amount: parseFloat(amount)
            });

            setMessage({ type: 'success', text: `Votre compte a été rechargé de ${amount} FCFA avec succès.` });
            setAmount('');
        } catch (err) {
            console.error(err);
            setMessage({ type: 'error', text: "Erreur lors du rechargement. Veuillez réessayer." });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-3xl mx-auto space-y-6 pt-4">
            <div className="flex items-center justify-between border-b pb-4">
                <h1 className="text-2xl font-bold text-gray-900">Rechargement de Compte</h1>
            </div>

            <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100">
                <div className="flex items-center space-x-4 mb-6">
                    <div className="bg-indigo-100 p-4 rounded-full">
                        <CreditCard className="w-8 h-8 text-indigo-600" />
                    </div>
                    <div>
                        <h2 className="text-xl font-bold text-gray-800">Créditer mon Mobility Pass</h2>
                        <p className="text-gray-500 text-sm">Approvisionnez votre compte via un moyen de paiement (Simulé).</p>
                    </div>
                </div>

                {message.text && (
                    <div className={`mb-6 p-4 rounded-lg flex items-center ${message.type === 'success' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
                        {message.type === 'success' ? <CheckCircle className="w-5 h-5 mr-3" /> : <AlertCircle className="w-5 h-5 mr-3" />}
                        {message.text}
                    </div>
                )}

                <form onSubmit={handleRecharge} className="space-y-6 max-w-md">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Montant de la recharge (FCFA)</label>
                        <div className="mt-1 relative rounded-md shadow-sm">
                            <input
                                type="number"
                                min="100"
                                step="100"
                                required
                                className="focus:ring-indigo-500 focus:border-indigo-500 block w-full pl-7 pr-12 text-lg py-3 border-gray-300 rounded-lg border bg-gray-50"
                                placeholder="Montant"
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                            />
                            <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                                <span className="text-gray-500 text-lg font-medium">FCFA</span>
                            </div>
                        </div>
                    </div>

                    <div className="flex space-x-3 mt-4">
                        {[1000, 2000, 5000].map(val => (
                            <button
                                type="button"
                                key={val}
                                onClick={() => setAmount(val)}
                                className="flex-1 bg-gray-100 text-gray-800 hover:bg-indigo-50 hover:text-indigo-600 font-medium py-2 rounded border border-gray-200 transition-colors"
                            >
                                {val}
                            </button>
                        ))}
                    </div>

                    <button
                        type="submit"
                        disabled={loading || !amount}
                        className="w-full flex justify-center items-center py-3 border border-transparent text-base font-medium rounded-lg text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all disabled:opacity-70 disabled:cursor-not-allowed mt-8"
                    >
                        {loading ? 'Traitement...' : 'Confirmer et Payer'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default BillingPage;
