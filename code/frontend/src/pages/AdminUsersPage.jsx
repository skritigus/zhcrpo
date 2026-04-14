import React, { useState, useEffect } from 'react';
import { getAllUsers, updateUserRoles } from '../services/api';
import '../styles/AdminUsersPage.css';

const AdminUsersPage = ({ showAppNotification, onRolesUpdated }) => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [updatingUserId, setUpdatingUserId] = useState(null);

    const rolesList = ['ADMIN', 'TRAINER', 'STUDENT'];

    const loadUsers = async () => {
        try {
            const data = await getAllUsers();
            setUsers(data);
        } catch (error) {
            showAppNotification("Ошибка загрузки пользователей", "error");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadUsers();
    }, []);

    const handleRoleToggle = (userId, roleName) => {
        setUsers(users.map(user => {
            if (user.id === userId) {
                const currentRoles = user.roleNames || [];
                const newRoles = currentRoles.includes(roleName)
                    ? currentRoles.filter(r => r !== roleName)
                    : [...currentRoles, roleName];
                return { ...user, roleNames: newRoles };
            }
            return user;
        }));
    };

    const handleUpdateRoles = async (userId) => {
        const user = users.find(u => u.id === userId);
        setUpdatingUserId(userId);
        try {
            await updateUserRoles(userId, user.roleNames);
            showAppNotification("Роли успешно обновлены", "success");
            if (onRolesUpdated) onRolesUpdated();
        } catch (error) {
            showAppNotification("Ошибка обновления ролей", "error");
            loadUsers(); // Revert changes
        } finally {
            setUpdatingUserId(null);
        }
    };

    if (loading) return <div className="text-center mt-5">Загрузка пользователей...</div>;

    return (
        <div className="users-page-container">
            <h1 className="page-title">Управление пользователями</h1>
            <div className="users-table-card">
                <table className="users-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Имя</th>
                            <th>Телефон</th>
                            <th>Текущие роли</th>
                            <th>Изменить роли</th>
                            <th>Действие</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map(user => (
                            <tr key={user.id} className="user-row">
                                <td>{user.id}</td>
                                <td>{user.name}</td>
                                <td>{user.phoneNumber}</td>
                                <td>
                                    <div className="role-badges">
                                        {(user.roleNames || []).map(role => (
                                            <span key={role} className={`role-badge ${role.toLowerCase()}`}>
                                                {role}
                                            </span>
                                        ))}
                                    </div>
                                </td>
                                <td>
                                    <div className="role-checkboxes">
                                        {rolesList.map(role => (
                                            <label key={role} className="role-checkbox-label">
                                                <input
                                                    type="checkbox"
                                                    checked={(user.roleNames || []).includes(role)}
                                                    onChange={() => handleRoleToggle(user.id, role)}
                                                />
                                                {role}
                                            </label>
                                        ))}
                                    </div>
                                </td>
                                <td>
                                    <button
                                        className="update-roles-btn"
                                        onClick={() => handleUpdateRoles(user.id)}
                                        disabled={updatingUserId === user.id}
                                    >
                                        {updatingUserId === user.id ? '...' : 'Сохранить'}
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AdminUsersPage;
