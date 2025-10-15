// src/pages/HallsPage.jsx
import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
    // fetchHalls, // Получаем из App.js
    createHall,
    updateHall,
    deleteHall
} from '../services/api';
import HallItem from '../components/Halls/HallItem';
import CreateHallModal from '../components/Halls/CreateHallModal';
import '../styles/HallsPage.css';

const HallsPage = ({ showAppNotification, appAllHalls, isLoadingAppLevelData, onMajorDataChange }) => {
    // Инициализируем halls всегда как массив, чтобы избежать ошибок с .length или .map
    const [halls, setHalls] = useState(appAllHalls || []);
    // Локальный isLoading теперь в основном для операций isProcessing,
    // а isLoadingAppLevelData контролирует начальную загрузку.
    // const [isLoading, setIsLoading] = useState(isLoadingAppLevelData); // Можно убрать, если используем isLoadingAppLevelData напрямую для отображения загрузчика

    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editingHall, setEditingHall] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [isProcessing, setIsProcessing] = useState(false); // Для блокировки кнопок во время CUD

    useEffect(() => {
        // console.log("HallsPage: appAllHalls prop or isLoadingAppLevelData changed.", appAllHalls, isLoadingAppLevelData);
        if (!isLoadingAppLevelData) {
            setHalls(appAllHalls || []); // Гарантируем, что halls всегда массив
        }
        // Локальный setIsLoading можно убрать, так как isLoadingAppLevelData теперь главный индикатор
    }, [appAllHalls, isLoadingAppLevelData]);

    const displayNotification = useCallback((...args) => {
        if (showAppNotification) showAppNotification(...args);
        else console.warn("HallsPage: showAppNotification not provided.", args);
    }, [showAppNotification]);

    const handleOpenCreateModal = () => {
        setEditingHall(null);
        setShowCreateModal(true);
    };

    const handleOpenEditModal = (hall) => {
        setEditingHall(hall); // Передаем весь объект зала
        setShowCreateModal(true);
    };

    const handleCloseModal = () => {
        setShowCreateModal(false);
        setEditingHall(null);
    };

    const handleSubmitHall = async (hallDataFromModal) => {
        setIsProcessing(true);
        try {
            // hallDataFromModal уже содержит id, если это редактирование
            if (hallDataFromModal.id) {
                // Передаем только те поля, которые можно редактировать (name, area)
                await updateHall(hallDataFromModal.id, { name: hallDataFromModal.name, area: hallDataFromModal.area });
                displayNotification("Зал успешно обновлен!", "success");
            } else {
                await createHall({ name: hallDataFromModal.name, area: hallDataFromModal.area });
                displayNotification("Зал успешно создан!", "success");
            }
            handleCloseModal();
            if (onMajorDataChange) onMajorDataChange(); // Обновляем данные на уровне App
        } catch (err) {
            console.error("HallsPage: Failed to save hall:", err);
            const message = err.data?.message || err.message || 'Неизвестная ошибка при сохранении зала.';
            displayNotification(`Ошибка: ${message}`, 'error');
        } finally {
            setIsProcessing(false);
        }
    };

    const handleDeleteHall = async (hallId) => {
        // Находим зал в текущем списке для проверки scheduleItemsId
        const hallToDelete = halls.find(h => h.id === hallId);

        if (window.confirm(`Вы уверены, что хотите удалить зал?`)) {
            setIsProcessing(true);
            try {
                await deleteHall(hallId);
                displayNotification("Зал успешно удален.", "success");
                if (onMajorDataChange) onMajorDataChange();
            } catch (err) {
                console.error("HallsPage: Failed to delete hall:", err);
                const message = err.data?.message || err.message || 'Неизвестная ошибка при удалении зала.';
                displayNotification(`Ошибка: ${message}`, 'error');
            } finally {
                setIsProcessing(false);
            }
        }
    };

    const filteredHalls = useMemo(() => {
        // Используем halls (локальное состояние, синхронизированное с appAllHalls)
        const hallsToFilter = halls || [];
        if (!searchTerm.trim()) {
            return hallsToFilter;
        }
        const searchTermLower = searchTerm.toLowerCase();
        return hallsToFilter.filter(hall =>
            hall && // Добавляем проверку на существование hall
            ((hall.name?.toLowerCase() || '').includes(searchTermLower) ||
                (hall.area?.toString() || '').includes(searchTermLower))
        );
    }, [halls, searchTerm]);

    // Показываем загрузку, если данные уровня приложения еще грузятся И локальных данных еще нет
    if (isLoadingAppLevelData && halls.length === 0) {
        return <div className="loading-message">Загрузка списка залов...</div>;
    }

    // Если загрузка завершена, но appAllHalls (и соответственно halls) пусты (например, ошибка в App.js)
    // Это условие может быть избыточным, если App.js уже показывает глобальную ошибку
    // if (!isLoadingAppLevelData && (!appAllHalls || appAllHalls.length === 0) && halls.length === 0) {
    //     return <div className="error-message">Ошибка: Данные о залах не были загружены или отсутствуют.</div>;
    // }

    return (
        <div className="halls-page-container">
            <div className="page-header">
                <h1>Залы</h1>
                <button
                    className="btn btn-primary"
                    onClick={handleOpenCreateModal}
                    disabled={isProcessing || isLoadingAppLevelData} // Блокируем также во время начальной загрузки
                >
                    {isProcessing ? 'Обработка...' : 'Добавить зал'}
                </button>
            </div>

            <div className="search-bar-container">
                <input
                    type="text"
                    placeholder="Поиск залов (название, площадь...)"
                    className="halls-search-input"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    disabled={isLoadingAppLevelData} // Блокируем поиск во время начальной загрузки
                />
            </div>

            {filteredHalls.length > 0 ? (
                <div className="halls-list-container"> {/* Убедитесь, что для этого класса есть стили, если нужна сетка */}
                    {filteredHalls.map(hall => (
                        <HallItem
                            key={hall.id}
                            hall={hall}
                            onEdit={handleOpenEditModal}
                            onDelete={handleDeleteHall}
                        />
                    ))}
                </div>
            ) : (
                // Сообщение показывается, если после загрузки и фильтрации список пуст
                !isLoadingAppLevelData && (
                    <p className="no-items-message">
                        {searchTerm.trim() ? "Залы не найдены по вашему запросу." : "Нет доступных залов. Вы можете добавить первый."}
                    </p>
                )
            )}

            <CreateHallModal
                show={showCreateModal}
                onClose={handleCloseModal}
                onSubmit={handleSubmitHall}
                existingHallData={editingHall}
            />
        </div>
    );
};

export default HallsPage;