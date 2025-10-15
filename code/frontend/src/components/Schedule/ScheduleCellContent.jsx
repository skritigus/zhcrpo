// src/components/Schedule/ScheduleCellContent.jsx
import React from 'react';
// ГЛОБАЛЬНЫЙ ИМПОРТ ОБЫЧНОГО CSS
import './../../styles/ScheduleCellContent.css'; // Убедитесь, что путь верный к ScheduleCellContent.css

const ScheduleCellContent = ({ item }) => {
    if (!item) {
        return null;
    }

    const danceStyle = item.group?.trainer?.danceStyle || 'N/A Стиль';
    const difficulty = item.group?.difficulty || 'N/A Уровень';
    const trainerName = item.group?.trainer?.name || 'N/A Тренер';

    // Формируем строку классов для сложности динамически
    let difficultyClass = "difficultyText"; // Базовый класс
    if (difficulty && typeof difficulty === 'string') { // Проверка, что difficulty строка
        const difficultySpecificClass = difficulty.toLowerCase();
        difficultyClass += ` ${difficultySpecificClass}`; // Например, "difficultyText pro"
    }


    return (
        // Используем обычные строковые имена классов
        <div className="cellWrapper"> {/* Имена классов как в ScheduleCellContent.css */}
            <div className="infoLine">
                <span className="styleText">{danceStyle}</span>
                <span className={difficultyClass}>{difficulty}</span>
            </div>
            <div className="trainerLine">
                <span className="labelText">Тренер:</span> {/* Добавил класс для "Тренер:" */}
                <span className="nameText">{trainerName}</span>
            </div>
        </div>
    );
};

export default ScheduleCellContent;