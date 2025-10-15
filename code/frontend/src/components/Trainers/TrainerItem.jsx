// src/components/Trainers/TrainerItem.jsx
import React from 'react';
import '../../styles/TrainerItem.css'; // Создадим этот CSS файл позже

const TrainerItem = ({ trainer, onEdit, onDelete }) => {
    if (!trainer) {
        return null;
    }

    return (
        <div className="trainer-item-wrapper">
            <div className="trainer-info">
                <h3 className="trainer-name">{trainer.name}</h3>
                <p className="trainer-detail">Стиль: {trainer.danceStyle || 'Не указан'}</p>
                <p className="trainer-detail">Телефон: {trainer.phoneNumber || 'Не указан'}</p>
                <p className="trainer-detail">
                    Групп: {trainer.groupsId ? trainer.groupsId.length : 0}
                </p>
            </div>
            <div className="trainer-actions">
                <button onClick={() => onEdit(trainer)} className="btn btn-edit">
                    Редактировать
                </button>
                <button onClick={() => onDelete(trainer.id)} className="btn btn-delete">
                    Удалить
                </button>
            </div>
        </div>
    );
};

export default TrainerItem;