import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';

const UserFormModal = ({ isOpen, onClose, onSubmit, user = null, isLoading = false }) => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        role: 'USER',
        password: ''
    });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (user) {
            setFormData({
                firstName: user.firstName || '',
                lastName: user.lastName || '',
                email: user.email || '',
                phoneNumber: user.phoneNumber || '',
                role: user.role || 'USER',
                password: ''
            });
        } else {
            setFormData({
                firstName: '',
                lastName: '',
                email: '',
                phoneNumber: '',
                role: 'USER',
                password: ''
            });
        }
        setErrors({});
    }, [user, isOpen]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        // Clear error for this field
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!formData.firstName.trim()) newErrors.firstName = 'Le prénom est requis';
        if (!formData.lastName.trim()) newErrors.lastName = 'Le nom est requis';
        if (!formData.email.trim()) newErrors.email = 'L\'email est requis';
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) newErrors.email = 'Email invalide';
        if (!formData.phoneNumber.trim()) newErrors.phoneNumber = 'Le téléphone est requis';
        if (!user && !formData.password) newErrors.password = 'Le mot de passe est requis';
        if (!user && formData.password && formData.password.length < 6) newErrors.password = 'Minimum 6 caractères';

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (validateForm()) {
            onSubmit(formData);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-3xl max-w-md w-full max-h-[90vh] overflow-y-auto shadow-2xl">
                {/* Header */}
                <div className="flex items-center justify-between p-6 border-b border-slate-100 sticky top-0 bg-white">
                    <h2 className="text-2xl font-bold text-slate-900">
                        {user ? 'Modifier l\'utilisateur' : 'Nouvel utilisateur'}
                    </h2>
                    <button
                        onClick={onClose}
                        className="p-2 hover:bg-slate-100 rounded-lg transition-colors"
                    >
                        <X className="w-5 h-5 text-slate-600" />
                    </button>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit} className="p-6 space-y-4">
                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2">Prénom</label>
                        <input
                            type="text"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 ${
                                errors.firstName
                                    ? 'border-red-300 focus:ring-red-500'
                                    : 'border-slate-200 focus:ring-blue-500'
                            }`}
                            placeholder="Jean"
                        />
                        {errors.firstName && <p className="text-red-600 text-sm mt-1">{errors.firstName}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2">Nom</label>
                        <input
                            type="text"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 ${
                                errors.lastName
                                    ? 'border-red-300 focus:ring-red-500'
                                    : 'border-slate-200 focus:ring-blue-500'
                            }`}
                            placeholder="Dupont"
                        />
                        {errors.lastName && <p className="text-red-600 text-sm mt-1">{errors.lastName}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2">Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            disabled={!!user}
                            className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 ${
                                errors.email
                                    ? 'border-red-300 focus:ring-red-500'
                                    : 'border-slate-200 focus:ring-blue-500'
                            } ${user ? 'bg-slate-50 cursor-not-allowed' : ''}`}
                            placeholder="jean@example.com"
                        />
                        {errors.email && <p className="text-red-600 text-sm mt-1">{errors.email}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2">Téléphone</label>
                        <input
                            type="tel"
                            name="phoneNumber"
                            value={formData.phoneNumber}
                            onChange={handleChange}
                            className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 ${
                                errors.phoneNumber
                                    ? 'border-red-300 focus:ring-red-500'
                                    : 'border-slate-200 focus:ring-blue-500'
                            }`}
                            placeholder="+33612345678"
                        />
                        {errors.phoneNumber && <p className="text-red-600 text-sm mt-1">{errors.phoneNumber}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2">Rôle</label>
                        <select
                            name="role"
                            value={formData.role}
                            onChange={handleChange}
                            className="w-full px-4 py-3 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="USER">Utilisateur</option>
                            <option value="ADMIN">Administrateur</option>
                        </select>
                    </div>

                    {!user && (
                        <div>
                            <label className="block text-sm font-semibold text-slate-700 mb-2">Mot de passe</label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                className={`w-full px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 ${
                                    errors.password
                                        ? 'border-red-300 focus:ring-red-500'
                                        : 'border-slate-200 focus:ring-blue-500'
                                }`}
                                placeholder="••••••"
                            />
                            {errors.password && <p className="text-red-600 text-sm mt-1">{errors.password}</p>}
                        </div>
                    )}

                    <div className="flex gap-3 pt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="flex-1 px-4 py-3 border border-slate-200 text-slate-700 font-semibold rounded-lg hover:bg-slate-50 transition-colors"
                        >
                            Annuler
                        </button>
                        <button
                            type="submit"
                            disabled={isLoading}
                            className="flex-1 px-4 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {isLoading ? 'Patientez...' : (user ? 'Modifier' : 'Créer')}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default UserFormModal;
