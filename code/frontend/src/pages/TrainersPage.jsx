// src/pages/TrainersPage.jsx
import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
    getAllTrainers,
    createTrainer,
    updateTrainer,
    deleteTrainer
} from '../services/api';
import TrainerItem from '../components/Trainers/TrainerItem';
import CreateTrainerModal from '../components/Trainers/CreateTrainerModal';
import '../styles/TrainersPage.css'; // Создадим этот CSS файл

const TrainersPage = ({ showAppNotification, appAllTrainers, isLoadingAppLevelData, onMajorDataChange }) => {
    const [trainers, setTrainers] = useState(appAllTrainers || []);
    const [isLoading, setIsLoading] = useState(isLoadingAppLevelData); // Используем флаг из App для начальной загрузки
    const [error, setError] = useState(null);

    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editingTrainer, setEditingTrainer] = useState(null); // Для передачи данных в модалку при редактировании
    const [searchTerm, setSearchTerm] = useState('');
    const [isProcessing, setIsProcessing] = useState(false); // Для операций CUD

    // Синхронизация с данными из App.js
    useEffect(() => {
        // console.log("TrainersPage: appAllTrainers prop changed", appAllTrainers);
        if (!isLoadingAppLevelData) { // Обновляем только если данные из App.js уже загружены
            setTrainers(appAllTrainers || []);
            setIsLoading(false); // Снимаем локальный флаг загрузки
        } else {
            setIsLoading(true); // Показываем загрузку, если App.js еще грузит
        }
    }, [appAllTrainers, isLoadingAppLevelData]);

    const displayNotification = useCallback((...args) => {
        if (showAppNotification) showAppNotification(...args);
    }, [showAppNotification]);

    const handleOpenCreateModal = () => {
        setEditingTrainer(null);
        setShowCreateModal(true);
    };

    const handleOpenEditModal = (trainer) => {
        setEditingTrainer(trainer);
        setShowCreateModal(true);
    };

    const handleCloseModal = () => {
        setShowCreateModal(false);
        setEditingTrainer(null);
    };

    const handleSubmitTrainer = async (trainerData) => {
        setIsProcessing(true);
        try {
            if (editingTrainer && editingTrainer.id) { // Режим редактирования
                await updateTrainer(editingTrainer.id, trainerData);
                displayNotification("Тренер успешно обновлен!", "success");
            } else { // Режим создания
                await createTrainer(trainerData);
                displayNotification("Тренер успешно создан!", "success");
            }
            handleCloseModal();
            if (onMajorDataChange) onMajorDataChange(); // Обновляем данные на уровне App
        } catch (err) {
            console.error("TrainersPage: Failed to save trainer:", err);
            displayNotification(`Ошибка сохранения тренера: ${err.message || 'Неизвестная ошибка'}`, 'error');
        } finally {
            setIsProcessing(false);
        }
    };

    const handleDeleteTrainer = async (trainerId) => {
        if (window.confirm(`Вы уверены, что хотите удалить тренера?`)) {
            setIsProcessing(true);
            try {
                await deleteTrainer(trainerId);
                displayNotification("Тренер успешно удален.", "success");
                if (onMajorDataChange) onMajorDataChange();
            } catch (err) {
                console.error("TrainersPage: Failed to delete trainer:", err);
                displayNotification(`Ошибка удаления тренера: ${err.message || 'Неизвестная ошибка'}`, 'error');
            } finally {
                setIsProcessing(false);
            }
        }
    };

    const filteredTrainers = useMemo(() => {
        const trainersToFilter = trainers || [];
        if (!searchTerm.trim()) {
            return trainersToFilter;
        }
        const searchTermLower = searchTerm.toLowerCase();
        return trainersToFilter.filter(trainer =>
            (trainer.name?.toLowerCase() || '').includes(searchTermLower) ||
            (trainer.phoneNumber?.toLowerCase() || '').includes(searchTermLower) ||
            (trainer.danceStyle?.toLowerCase() || '').includes(searchTermLower)
        );
    }, [trainers, searchTerm]);


    if (isLoading && (!trainers || trainers.length === 0) ) {
        return <div className="loading-message">Загрузка списка тренеров...</div>;
    }
    if (error) { // Показываем ошибку, если она была установлена при загрузке в App.js
        return <div className="error-message">Ошибка загрузки тренеров: {error}</div>;
    }


    return (
        <div className="trainers-page-container">
            <div className="page-header">
                <h1>Тренеры</h1>
                <button className="btn btn-primary" onClick={handleOpenCreateModal} disabled={isProcessing}>
                    {isProcessing ? 'Обработка...' : 'Добавить тренера'}
                </button>
            </div>

            <div className="search-bar-container">
                <input
                    type="text"
                    placeholder="Поиск тренеров (имя, телефон, стиль...)"
                    className="trainers-search-input"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>

            {filteredTrainers.length > 0 ? (
                <div className="trainers-list">
                    {filteredTrainers.map(trainer => (
                        <TrainerItem
                            key={trainer.id}
                            trainer={trainer}
                            onEdit={handleOpenEditModal}
                            onDelete={handleDeleteTrainer}
                        />
                    ))}
                </div>
            ) : (
                <p className="no-items-message">
                    {searchTerm.trim() ? "Тренеры не найдены по вашему запросу." : "Нет доступных тренеров."}
                </p>
            )}

            <CreateTrainerModal
                show={showCreateModal}
                onClose={handleCloseModal}
                onSubmit={handleSubmitTrainer}
                existingTrainerData={editingTrainer}
            />
        </div>
    );
};

export default TrainersPage;