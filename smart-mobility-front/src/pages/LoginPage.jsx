import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { ArrowRight } from 'lucide-react';
import LoginHeader from '../components/LoginPage/LoginHeader';
import { AuthFormFields, RegistrationFields } from '../components/LoginPage/FormInput';
import ErrorAlert from '../components/ErrorAlert';

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
    const [showPassword, setShowPassword] = useState(false);

    const { login, register } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            if (isLogin) {
                const result = await login(formData.email, formData.password);
                if (result.success) {
                    const userRole = result.user?.role || 'USER';
                    const redirectPath = userRole === 'ADMIN' ? '/admin' : '/dashboard/trips';
                    navigate(redirectPath);
                } else {
                    setError(result.error || 'Erreur de connexion');
                }
            } else {
                const result = await register(formData);
                if (result.success) {
                    const loginResult = await login(formData.email, formData.password);
                    if (loginResult.success) {
                        const userRole = loginResult.user?.role || 'USER';
                        const redirectPath = userRole === 'ADMIN' ? '/admin' : '/dashboard/trips';
                        navigate(redirectPath);
                    } else {
                        setIsLogin(true);
                        setError('Inscription réussie. Veuillez vous connecter.');
                    }
                } else {
                    setError(result.error || 'Erreur lors de l\'inscription');
                }
            }
        } catch (err) {
            setError(err.message || 'Une erreur est survenue');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-50 relative overflow-hidden px-4 sm:px-6 lg:px-8">
            {/* Background Decorations */}
            <div className="absolute top-[-10%] left-[-10%] w-96 h-96 bg-indigo-400 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-pulse-slow"></div>
            <div className="absolute bottom-[-10%] right-[-10%] w-96 h-96 bg-blue-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-pulse-slow object-right"></div>

            <div className="max-w-md w-full space-y-8 bg-white/80 backdrop-blur-xl p-10 rounded-3xl shadow-2xl border border-white/50 relative z-10 animate-fade-in">
                <LoginHeader isLogin={isLogin} />

                <form className="mt-8 space-y-6 animate-slide-up" onSubmit={handleSubmit}>
                    <ErrorAlert error={error} />

                    <div className="space-y-4">
                        {!isLogin && <RegistrationFields formData={formData} handleChange={handleChange} errors={{}} />}
                        <AuthFormFields formData={formData} handleChange={handleChange} errors={{}} />
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
                        <button 
                            type="button" 
                            onClick={() => { setIsLogin(!isLogin); setError(''); }} 
                            className="text-sm font-medium text-indigo-600 hover:text-indigo-500 transition-colors"
                        >
                            {isLogin ? "Pas encore de compte ? S'inscrire" : 'Déjà un compte ? Se connecter'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
