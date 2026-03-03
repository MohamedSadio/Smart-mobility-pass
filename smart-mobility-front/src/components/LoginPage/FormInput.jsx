import React from 'react';
import { Mail, Lock, User, Phone } from 'lucide-react';

/**
 * Reusable form input component for authentication
 */
const FormInput = ({ 
    name, 
    type = 'text', 
    placeholder, 
    value, 
    onChange, 
    icon: Icon,
    required = false,
    error = null
}) => {
    return (
        <div>
            <label className="sr-only">{placeholder}</label>
            <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Icon className="h-5 w-5 text-slate-400" />
                </div>
                <input
                    name={name}
                    type={type}
                    required={required}
                    value={value}
                    onChange={onChange}
                    placeholder={placeholder}
                    className={`block w-full pl-10 pr-3 py-3 border rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 bg-white/50 backdrop-blur-sm transition-all text-sm outline-none ${
                        error ? 'border-red-300' : 'border-slate-200'
                    }`}
                />
            </div>
            {error && <p className="text-red-500 text-xs mt-1">{error}</p>}
        </div>
    );
};

/**
 * Registration form fields
 */
export const RegistrationFields = ({ formData, handleChange, errors }) => (
    <div className="grid grid-cols-2 gap-4">
        <FormInput
            name="firstName"
            type="text"
            placeholder="Prénom"
            value={formData.firstName}
            onChange={handleChange}
            icon={User}
            required
            error={errors.firstName}
        />
        <FormInput
            name="lastName"
            type="text"
            placeholder="Nom"
            value={formData.lastName}
            onChange={handleChange}
            icon={User}
            required
            error={errors.lastName}
        />
        <div className="col-span-2">
            <FormInput
                name="phoneNumber"
                type="tel"
                placeholder="Numéro de téléphone"
                value={formData.phoneNumber}
                onChange={handleChange}
                icon={Phone}
                required
                error={errors.phoneNumber}
            />
        </div>
    </div>
);

/**
 * Common auth form fields (email & password)
 */
export const AuthFormFields = ({ formData, handleChange, errors }) => (
    <div className="space-y-4">
        <FormInput
            name="email"
            type="email"
            placeholder="Adresse email"
            value={formData.email}
            onChange={handleChange}
            icon={Mail}
            required
            error={errors.email}
        />
        <FormInput
            name="password"
            type="password"
            placeholder="Mot de passe"
            value={formData.password}
            onChange={handleChange}
            icon={Lock}
            required
            error={errors.password}
        />
    </div>
);

export default FormInput;
