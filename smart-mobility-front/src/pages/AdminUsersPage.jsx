import React, { useState, useEffect } from 'react';
import { Users, Plus, Edit, Trash, Search } from 'lucide-react';
import userService from '../services/userService';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorAlert from '../components/ErrorAlert';
import UserFormModal from '../components/UserFormModal';

const AdminUsersPage = () => {
    const [users, setUsers] = useState([]);
    const [filteredUsers, setFilteredUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        fetchUsers();
    }, []);

    useEffect(() => {
        // Filtrer les utilisateurs selon le terme de recherche
        const filtered = users.filter(user =>
            user.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.lastName.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.email.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setFilteredUsers(filtered);
    }, [searchTerm, users]);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await userService.getAllUsers();
            setUsers(response);
        } catch (err) {
            setError('Erreur lors du chargement des utilisateurs');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (userId) => {
        if (window.confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur?')) {
            try {
                await userService.deleteUser(userId);
                setUsers(users.filter(u => u.id !== userId));
                setError(null);
            } catch (err) {
                setError('Erreur lors de la suppression');
                console.error(err);
            }
        }
    };

    const handleOpenCreateModal = () => {
        setSelectedUser(null);
        setIsModalOpen(true);
    };

    const handleOpenEditModal = (user) => {
        setSelectedUser(user);
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setSelectedUser(null);
    };

    const handleSubmitForm = async (formData) => {
        setIsSubmitting(true);
        try {
            if (selectedUser) {
                // Edit existing user
                const updatedUser = await userService.updateUser(selectedUser.id, {
                    firstName: formData.firstName,
                    lastName: formData.lastName,
                    phoneNumber: formData.phoneNumber,
                    role: formData.role
                });
                setUsers(users.map(u => u.id === selectedUser.id ? updatedUser : u));
            } else {
                // Create new user
                const newUser = await userService.createUser(formData);
                setUsers([...users, newUser]);
            }
            setError(null);
            handleCloseModal();
        } catch (err) {
            setError(selectedUser ? 'Erreur lors de la modification' : 'Erreur lors de la création');
            console.error(err);
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) {
        return <LoadingSpinner size="lg" message="Chargement des utilisateurs..." />;
    }

    return (
        <div className="space-y-8 animate-fade-in">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <div className="flex items-center gap-4">
                        <div className="bg-blue-100 p-4 rounded-2xl">
                            <Users className="w-8 h-8 text-blue-600" />
                        </div>
                        <div>
                            <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
                                Gestion des utilisateurs
                            </h1>
                            <p className="text-slate-500 mt-1">
                                {users.length} utilisateur{users.length > 1 ? 's' : ''} enregistré{users.length > 1 ? 's' : ''}
                            </p>
                        </div>
                    </div>
                </div>
                <button 
                    onClick={handleOpenCreateModal}
                    className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-xl transition-all shadow-lg shadow-blue-600/20"
                >
                    <Plus className="w-5 h-5" />
                    Nouvel utilisateur
                </button>
            </div>

            {error && <ErrorAlert message={error} onClose={() => setError(null)} />}

            {/* Search Bar */}
            <div className="relative">
                <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
                <input
                    type="text"
                    placeholder="Rechercher par nom ou email..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full pl-12 pr-4 py-3 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
            </div>

            {/* Users Table */}
            <div className="bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden">
                <table className="w-full">
                    <thead className="bg-slate-50 border-b border-slate-100">
                        <tr>
                            <th className="px-6 py-4 text-left text-sm font-bold text-slate-700">Nom</th>
                            <th className="px-6 py-4 text-left text-sm font-bold text-slate-700">Email</th>
                            <th className="px-6 py-4 text-left text-sm font-bold text-slate-700">Rôle</th>
                            <th className="px-6 py-4 text-left text-sm font-bold text-slate-700">Statut</th>
                            <th className="px-6 py-4 text-center text-sm font-bold text-slate-700">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                        {filteredUsers.map((user) => (
                            <tr key={user.id} className="hover:bg-slate-50 transition-colors">
                                <td className="px-6 py-4 text-sm text-slate-900 font-semibold">
                                    {user.firstName} {user.lastName}
                                </td>
                                <td className="px-6 py-4 text-sm text-slate-600">{user.email}</td>
                                <td className="px-6 py-4 text-sm">
                                    <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                                        user.role === 'ADMIN'
                                            ? 'bg-red-100 text-red-700'
                                            : 'bg-blue-100 text-blue-700'
                                    }`}>
                                        {user.role === 'ADMIN' ? 'Administrateur' : 'Utilisateur'}
                                    </span>
                                </td>
                                <td className="px-6 py-4 text-sm">
                                    <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                                        user.status === 'ACTIVE'
                                            ? 'bg-green-100 text-green-700'
                                            : 'bg-yellow-100 text-yellow-700'
                                    }`}>
                                        {user.status === 'ACTIVE' ? 'Actif' : 'Inactif'}
                                    </span>
                                </td>
                                <td className="px-6 py-4 text-sm">
                                    <div className="flex items-center justify-center gap-2">
                                        <button 
                                            onClick={() => handleOpenEditModal(user)}
                                            className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                                        >
                                            <Edit className="w-4 h-4" />
                                        </button>
                                        <button
                                            onClick={() => handleDelete(user.id)}
                                            className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                                        >
                                            <Trash className="w-4 h-4" />
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

                {filteredUsers.length === 0 && (
                    <div className="text-center py-12">
                        <Users className="w-12 h-12 text-slate-300 mx-auto mb-4" />
                        <p className="text-slate-500 font-semibold">Aucun utilisateur trouvé</p>
                    </div>
                )}
            </div>

            {/* User Form Modal */}
            <UserFormModal
                isOpen={isModalOpen}
                onClose={handleCloseModal}
                onSubmit={handleSubmitForm}
                user={selectedUser}
                isLoading={isSubmitting}
            />
        </div>
    );
};

export default AdminUsersPage;
