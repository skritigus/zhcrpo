// src/components/Schedule/GroupOptionContent.jsx
import React from 'react';
// ИМПОРТИРУЕМ КАК ОБЫЧНЫЙ CSS
import './../../styles/GroupOptionContent.css'; // Убедитесь, что путь правильный к GroupOptionContent.css

const GroupOptionContent = ({ item }) => {
    if (!item || !item.group) {
        return <span className="optionContentWrapper">N/A Группа</span>; // Используем строковые классы
    }

    const group = item.group;
    const danceStyle = group.trainer?.danceStyle;
    const difficulty = group.difficulty;
    const trainerName = group.trainer?.name;

    // Динамический класс для сложности
    let difficultyClassName = "optionDifficulty"; // Базовый класс
    if (difficulty && typeof difficulty === 'string') {
        difficultyClassName += ` ${difficulty.toLowerCase()}`; // например, "optionDifficulty pro"
    }

    return (
        // Используем обычные строковые имена классов
        <div className="optionContentWrapper">
            <div className="mainInfoLine">
                {danceStyle && (
                    <span className="optionDanceStyle">
                        {danceStyle}
                    </span>
                )}
                {!danceStyle && difficulty && <span className="flexGrowPlaceholder"></span>}

                {difficulty && (
                    <span className={difficultyClassName}> {/* Используем собранный класс */}
                        {difficulty}
                    </span>
                )}
            </div>
            {trainerName && (
                <div className="optionTrainerName">
                    Тренер: {trainerName}
                </div>
            )}
            {!danceStyle && !difficulty && !trainerName && group.name && (
                <div className="optionGroupName">{group.name}</div>
            )}
        </div>
    );
};

export default GroupOptionContent;