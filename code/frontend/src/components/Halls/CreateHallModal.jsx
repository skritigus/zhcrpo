// src/components/Halls/CreateHallModal.jsx
import React, { useState, useEffect } from 'react';
import '../../styles/CreateHallModal.css'; // Создадим этот CSS

const CreateHallModal = ({ show, onClose, onSubmit, existingHallData = null }) => {
    const [name, setName] = useState('');
    const [area, setArea] = useState(''); // Площадь может быть числом
    const [error, setError] = useState('');

    const isEditMode = !!existingHallData;

    useEffect(() => {
        if (show) {
            setError('');
            if (isEditMode && existingHallData) {
                setName(existingHallData.name || '');
                setArea(existingHallData.area?.toString() || ''); // Преобразуем в строку для input type="number" или text
            } else {
                setName('');
                setArea('');
            }
        }
    }, [show, existingHallData, isEditMode]);

    if (!show) {
        return null;
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        const areaValue = parseFloat(area);
        if (!name.trim() || isNaN(areaValue) || areaValue <= 0) {
            setError("Пожалуйста, заполните название и корректную площадь (положительное число).");
            return;
        }
        setError('');
        const hallData = { name, area: areaValue };
        const submissionData = isEditMode ? { id: existingHallData.id, ...hallData } : hallData;
        onSubmit(submissionData);
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content create-hall-modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close-btn" onClick={onClose}>×</button>
                <h2>{isEditMode ? "Редактировать зал" : "Добавить новый зал"}</h2>
                {error && <p className="modal-error-text">{error}</p>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="hallName">Название зала:</label>
                        <input
                            type="text"
                            id="hallName"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="hallArea">Площадь (кв.м):</label>
                        <input
                            type="number" // Используем type="number" для площади
                            id="hallArea"
                            value={area}
                            onChange={(e) => setArea(e.target.value)}
                            min="1" // Минимальное значение
                            required
                        />
                    </div>
                    <div className="modal-actions">
                        <button type="button" className="btn btn-cancel" onClick={onClose}>Отмена</button>
                        <button type="submit" className="btn btn-primary">
                            {isEditMode ? "Сохранить" : "Создать"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateHallModal;