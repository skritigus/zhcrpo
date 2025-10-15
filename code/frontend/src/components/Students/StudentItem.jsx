// src/components/Students/StudentItem.jsx
import React from 'react';
import '../../styles/StudentItem.css'; // Создадим этот CSS

const StudentItem = ({ student, onEdit, onDelete }) => {
    if (!student) {
        return null;
    }

    // Отображение ID групп может быть полезно для администратора, но можно убрать
    const groupsDisplay = student.groupsId && student.groupsId.length > 0
        ? student.groupsId.join(', ')
        : 'Нет';

    return (
        <div className="student-item-wrapper">
            <div className="student-info">
                <h3 className="student-name">{student.name}</h3>
                <p className="student-detail">Телефон: {student.phoneNumber || 'Не указан'}</p>
                {/* <p className="student-detail">ID Групп: {groupsDisplay}</p> */}
                {/* Закомментировал отображение ID групп, т.к. это может быть не нужно пользователю */}
            </div>
            <div className="student-actions">
                <button onClick={() => onEdit(student)} className="btn btn-edit">
                    Редактировать
                </button>
                <button onClick={() => onDelete(student.id)} className="btn btn-delete">
                    Удалить
                </button>
            </div>
        </div>
    );
};

export default StudentItem;