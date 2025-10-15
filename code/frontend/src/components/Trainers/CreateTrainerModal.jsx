// src/components/Trainers/CreateTrainerModal.jsx
import React, { useState, useEffect } from 'react';
import '../../styles/CreateTrainerModal.css'; // Создадим этот CSS файл позже

const CreateTrainerModal = ({ show, onClose, onSubmit, existingTrainerData = null }) => {
    const [name, setName] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [danceStyle, setDanceStyle] = useState('');
    const [error, setError] = useState('');

    const isEditMode = !!existingTrainerData;

    useEffect(() => {
        if (show) {
            setError(''); // Сбрасываем ошибку при открытии
            if (isEditMode && existingTrainerData) {
                setName(existingTrainerData.name || '');
                setPhoneNumber(existingTrainerData.phoneNumber || '');
                setDanceStyle(existingTrainerData.danceStyle || '');
            } else {
                setName('');
                setPhoneNumber('');
                setDanceStyle('');
            }
        }
    }, [show, existingTrainerData, isEditMode]);

    if (!show) {
        return null;
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!name.trim() || !phoneNumber.trim() || !danceStyle.trim()) {
            setError("Пожалуйста, заполните все поля.");
            return;
        }
        setError('');
        const trainerData = { name, phoneNumber, danceStyle };
        // Если это режим редактирования, передаем ID
        const submissionData = isEditMode ? { id: existingTrainerData.id, ...trainerData } : trainerData;
        onSubmit(submissionData);
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content create-trainer-modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close-btn" onClick={onClose}>×</button>
                <h2>{isEditMode ? "Редактировать тренера" : "Добавить нового тренера"}</h2>
                {error && <p className="modal-error-text">{error}</p>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="trainerName">Имя:</label>
                        <input
                            type="text"
                            id="trainerName"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="trainerPhoneNumber">Номер телефона:</label>
                        <input
                            type="text"
                            id="trainerPhoneNumber"
                            value={phoneNumber}
                            onChange={(e) => setPhoneNumber(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="trainerDanceStyle">Стиль танца:</label>
                        <input
                            type="text"
                            id="trainerDanceStyle"
                            value={danceStyle}
                            onChange={(e) => setDanceStyle(e.target.value)}
                            required
                        />
                    </div>
                    <div className="modal-actions">
                        <button type="button" className="btn btn-cancel" onClick={onClose}>Отмена</button>
                        <button type="submit" className="btn btn-primary">
                            {isEditMode ? "Сохранить изменения" : "Создать тренера"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateTrainerModal;