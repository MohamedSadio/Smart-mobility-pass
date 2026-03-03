import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Shield, BarChart3, Users, Settings } from 'lucide-react';
import { Link } from 'react-router-dom';

const AdminPage = () => {
    const { user } = useAuth();

    const adminFeatures = [
        {
            icon: Users,
            title: 'Gestion des utilisateurs',
            description: 'Gérer les comptes utilisateurs et administrateurs',
            link: '/admin/users'
        },
        {
            icon: BarChart3,
            title: 'Statistiques',
            description: 'Consulter les rapports et statistiques',
            link: '/admin/stats'
        },
        {
            icon: Settings,
            title: 'Configuration',
            description: 'Paramètres du système et configuration',
            link: '/admin/settings'
        }
    ];

    return (
        <div className="space-y-8 animate-fade-in">
            {/* Header */}
            <div className="flex items-center gap-4">
                <div className="bg-red-100 p-4 rounded-2xl">
                    <Shield className="w-8 h-8 text-red-600" />
                </div>
                <div>
                    <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
                        Panneau d'administration
                    </h1>
                    <p className="text-slate-500 mt-1">
                        Bienvenue {user?.firstName}, gérez l'application Smart Mobility Pass
                    </p>
                </div>
            </div>

            {/* Stats Overview */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 hover:shadow-xl transition-all">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm font-medium text-slate-500 uppercase">Utilisateurs actifs</p>
                            <h3 className="text-3xl font-bold text-slate-900 mt-2">---</h3>
                        </div>
                        <Users className="w-10 h-10 text-blue-500" />
                    </div>
                </div>
                <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 hover:shadow-xl transition-all">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm font-medium text-slate-500 uppercase">Trajets aujourd'hui</p>
                            <h3 className="text-3xl font-bold text-slate-900 mt-2">---</h3>
                        </div>
                        <BarChart3 className="w-10 h-10 text-green-500" />
                    </div>
                </div>
                <div className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 hover:shadow-xl transition-all">
                    <div className="flex items-center justify-between">
                        <div>
                            <p className="text-sm font-medium text-slate-500 uppercase">Revenu total</p>
                            <h3 className="text-3xl font-bold text-slate-900 mt-2">---</h3>
                        </div>
                        <BarChart3 className="w-10 h-10 text-purple-500" />
                    </div>
                </div>
            </div>

            {/* Admin Features */}
            <div>
                <h2 className="text-2xl font-bold text-slate-900 mb-4">Fonctionnalités d'administration</h2>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {adminFeatures.map((feature) => {
                        const Icon = feature.icon;
                        return (
                            <Link
                                key={feature.title}
                                to={feature.link}
                                className="bg-white rounded-3xl p-6 shadow-sm border border-slate-100 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 group"
                            >
                                <div className="bg-indigo-100 p-3 rounded-xl inline-block mb-4 group-hover:bg-indigo-200 transition-colors">
                                    <Icon className="w-6 h-6 text-indigo-600" />
                                </div>
                                <h3 className="text-lg font-bold text-slate-900 mb-2">{feature.title}</h3>
                                <p className="text-sm text-slate-600">{feature.description}</p>
                            </Link>
                        );
                    })}
                </div>
            </div>
        </div>
    );
};

export default AdminPage;
