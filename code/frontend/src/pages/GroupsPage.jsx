// src/pages/GroupsPage.jsx
import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
    createGroup,
    deleteGroup as apiDeleteGroup,
} from '../services/api';
import GroupItem from '../components/Groups/GroupItem';
import CreateGroupModal from '../components/Groups/CreateGroupModal';
import '../styles/GroupsPage.css';

const GroupsPage = ({
                        appGroups,
                        appAllStudents,
                        appAllTrainers,
                        isLoadingAppLevelData,
                        showAppNotification,
                        onMajorDataChange
                    }) => {

    const [showCreateModal, setShowCreateModal] = useState(false);
    const [isProcessing, setIsProcessing] = useState(false);
    const [groupSearchTerm, setGroupSearchTerm] = useState('');
    const [expandedGroupId, setExpandedGroupId] = useState(null);

    const displayNotification = useCallback((...args) => {
        if (showAppNotification) {
            showAppNotification(...args);
        } else {
            console.warn("Fallback Notification (GroupsPage):", args[0], args[1]);
        }
    }, [showAppNotification]);

    const handleCreateGroup = useCallback(async (groupData) => {
        setIsProcessing(true);
        try {
            await createGroup(groupData);
            displayNotification("Группа успешно создана!", "success");
            setShowCreateModal(false);
            if (onMajorDataChange) onMajorDataChange();
        } catch (err) {
            console.error("GroupsPage: Failed to create group:", err);
            displayNotification(`Ошибка создания группы: ${err.message || 'Неизвестная ошибка'}`, 'error');
        } finally {
            setIsProcessing(false);
        }
    }, [displayNotification, onMajorDataChange]);

    const handleDeleteGroup = useCallback(async (groupId) => {
        if (window.confirm(`Вы уверены, что хотите удалить группу?`)) { // Обновил сообщение для ясности
            setIsProcessing(true);
            try {
                await apiDeleteGroup(groupId);
                displayNotification("Группа успешно удалена.", "success");
                if (expandedGroupId === groupId) {
                    setExpandedGroupId(null);
                }
                if (onMajorDataChange) onMajorDataChange();
            } catch (err) {
                console.error("GroupsPage: Failed to delete group:", err);
                displayNotification(`Ошибка удаления группы: ${err.message || 'Неизвестная ошибка'}`, 'error');
            } finally {
                setIsProcessing(false);
            }
        }
    }, [displayNotification, onMajorDataChange, expandedGroupId]);


    const filteredGroups = useMemo(() => {
        const groupsToFilter = appGroups || [];
        if (!groupSearchTerm.trim()) {
            return groupsToFilter;
        }
        const searchTermLower = groupSearchTerm.toLowerCase();
        return groupsToFilter.filter(group => {
            if (!group) return false;
            const trainerName = group.trainer?.name?.toLowerCase() || '';
            const danceStyle = group.trainer?.danceStyle?.toLowerCase() || '';
            const difficulty = group.difficulty?.toLowerCase() || '';
            const groupName = group.name?.toLowerCase() || ''; // Предполагаем, что у группы может быть свое имя
            return groupName.includes(searchTermLower) ||
                trainerName.includes(searchTermLower) ||
                danceStyle.includes(searchTermLower) ||
                difficulty.includes(searchTermLower);
        });
    }, [appGroups, groupSearchTerm]);

    const handleToggleGroup = useCallback((groupId) => {
        setExpandedGroupId(prevExpandedId => (prevExpandedId === groupId ? null : groupId));
    }, []);

    const renderGroupItem = (group) => (
        group &&
        <GroupItem
            key={group.id}
            group={group}
            allStudents={appAllStudents || []}
            onForceRefreshList={onMajorDataChange}
            showNotification={displayNotification}
            onDeleteGroup={handleDeleteGroup}
            isCurrentlyExpanded={group.id === expandedGroupId}
            onToggle={() => handleToggleGroup(group.id)}
        />
    );

    return (
        <>
            <div className="groups-page-container">
                <div className="page-header"> {/* Используем общий класс, если он есть, или groups-header */}
                    <h1>Группы</h1>
                    <button
                        className="btn btn-primary"
                        onClick={() => setShowCreateModal(true)}
                        disabled={isProcessing || isLoadingAppLevelData} // Блокируем, если идет глобальная загрузка
                    >
                        {isProcessing ? 'Обработка...' : 'Создать группу'}
                    </button>
                </div>
                <div className="search-bar-container">
                    <input
                        type="text"
                        placeholder="Поиск групп (название, тренер, стиль, сложность...)"
                        className="groups-search-input" // Убедитесь, что стили есть
                        value={groupSearchTerm}
                        onChange={(e) => setGroupSearchTerm(e.target.value)}
                        disabled={isLoadingAppLevelData} // Блокируем поиск во время глобальной загрузки
                    />
                </div>

                {/* Условное отображение списка или сообщений */}
                {isLoadingAppLevelData ? (
                    <div className="loading-message">Загрузка данных...</div>
                ) : (!appGroups) ? (
                    // Эта ситуация означает, что appGroups не были переданы или null/undefined после загрузки
                    // что указывает на более глубокую проблему в App.js или ошибку API
                    <div className="error-message">Ошибка: Данные о группах не доступны.</div>
                ) : (
                    filteredGroups.length > 0 ? (
                        <div className="groups-list">
                            {filteredGroups.map(renderGroupItem)}
                        </div>
                    ) : (
                        <p className="no-items-message"> {/* Используем общий класс, если есть */}
                            {groupSearchTerm.trim() ? "Группы не найдены по вашему запросу." : "Нет доступных групп. Создайте первую!"}
                        </p>
                    )
                )}

                <CreateGroupModal
                    show={showCreateModal}
                    onClose={() => setShowCreateModal(false)}
                    onSubmit={handleCreateGroup}
                    allTrainers={appAllTrainers || []}
                    allStudents={appAllStudents || []}
                />
            </div>
        </>
    );
};
export default GroupsPage;
