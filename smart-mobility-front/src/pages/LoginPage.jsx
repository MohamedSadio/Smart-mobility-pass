import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Mail, Lock, User, ArrowRight, Activity, Phone } from 'lucide-react';

const LoginPage = () => {
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        phoneNumber: '',
        role: 'USER'
    });
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const { login, register } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        if (isLogin) {
            const result = await login(formData.email, formData.password);
            if (result.success) navigate('/');
            else setError(result.error);
        } else {
            const result = await register(formData);
            if (result.success) {
                // Auto-login after register
                const loginResult = await login(formData.email, formData.password);
                if (loginResult.success) navigate('/');
                else {
                    setIsLogin(true);
                    setError('Inscription réussie. Veuillez vous connecter.');
                }
            } else {
                setError(result.error);
            }
        }
        setIsLoading(false);
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-50 relative overflow-hidden px-4 sm:px-6 lg:px-8">
            {/* Background Decorations */}
            <div className="absolute top-[-10%] left-[-10%] w-96 h-96 bg-indigo-400 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-pulse-slow"></div>
            <div className="absolute bottom-[-10%] right-[-10%] w-96 h-96 bg-blue-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-pulse-slow object-right"></div>

            <div className="max-w-md w-full space-y-8 bg-white/80 backdrop-blur-xl p-10 rounded-3xl shadow-2xl border border-white/50 relative z-10 animate-fade-in">
                <div className="text-center">
                    <div className="mx-auto bg-indigo-600 text-white w-16 h-16 rounded-2xl flex items-center justify-center shadow-lg transform transition hover:scale-105">
                        <Activity className="w-8 h-8" />
                    </div>
                    <h2 className="mt-6 text-3xl font-extrabold text-slate-800 tracking-tight">
                        Smart Mobility Pass
                    </h2>
                    <p className="mt-2 text-sm text-slate-500">
                        {isLogin ? 'Connectez-vous pour gérer vos trajets' : 'Créez votre compte pour commencer'}
                    </p>
                </div>

                <form className="mt-8 space-y-6 animate-slide-up" onSubmit={handleSubmit}>
                    {error && (
                        <div className="bg-red-50/80 backdrop-blur-sm border border-red-200 text-red-600 p-4 rounded-xl text-sm text-center shadow-sm animate-fade-in">
                            {error}
                        </div>
                    )}

                    <div className="space-y-4">
                        {!isLogin && (
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="sr-only">Prénom</label>
                                    <div className="relative">
                                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                            <User className="h-5 w-5 text-slate-400" />
                                        </div>
                                        <input name="firstName" type="text" required onChange={handleChange} className="block w-full pl-10 pr-3 py-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white/50 backdrop-blur-sm transition-all text-sm outline-none" placeholder="Prénom" />
                                    </div>
                                </div>
                                <div>
                                    <label className="sr-only">Nom</label>
                                    <div className="relative">
                                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                            <User className="h-5 w-5 text-slate-400" />
                                        </div>
                                        <input name="lastName" type="text" required onChange={handleChange} className="block w-full pl-10 pr-3 py-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white/50 backdrop-blur-sm transition-all text-sm outline-none" placeholder="Nom" />
                                    </div>
                                </div>
                            </div>
                        )}
                        {!isLogin && (
                            <div>
                                <label className="sr-only">Numéro de téléphone</label>
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                        <Phone className="h-5 w-5 text-slate-400" />
                                    </div>
                                    <input name="phoneNumber" type="tel" required onChange={handleChange} className="block w-full pl-10 pr-3 py-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white/50 backdrop-blur-sm transition-all text-sm outline-none" placeholder="Numéro de téléphone" />
                                </div>
                            </div>
                        )}
                        <div>
                            <label className="sr-only">Email address</label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Mail className="h-5 w-5 text-slate-400" />
                                </div>
                                <input name="email" type="email" required onChange={handleChange} className="block w-full pl-10 pr-3 py-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white/50 backdrop-blur-sm transition-all text-sm outline-none" placeholder="Adresse email" />
                            </div>
                        </div>
                        <div>
                            <label className="sr-only">Password</label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Lock className="h-5 w-5 text-slate-400" />
                                </div>
                                <input name="password" type="password" required onChange={handleChange} className="block w-full pl-10 pr-3 py-3 border border-slate-200 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white/50 backdrop-blur-sm transition-all text-sm outline-none" placeholder="Mot de passe" />
                            </div>
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading}
                        className={`group relative w-full flex justify-center py-3.5 px-4 border border-transparent text-sm font-semibold rounded-xl text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 shadow-md shadow-indigo-600/30 transition-all ${isLoading ? 'opacity-70 cursor-wait' : 'hover:-translate-y-0.5'}`}
                    >
                        {isLoading ? 'Patientez...' : (isLogin ? 'Se connecter' : "S'inscrire")}
                        {!isLoading && <ArrowRight className="ml-2 w-4 h-4 group-hover:translate-x-1 transition-transform" />}
                    </button>

                    <div className="text-center mt-4">
                        <button type="button" onClick={() => { setIsLogin(!isLogin); setError(''); }} className="text-sm font-medium text-indigo-600 hover:text-indigo-500 transition-colors">
                            {isLogin ? "Pas encore de compte ? S'inscrire" : 'Déjà un compte ? Se connecter'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
