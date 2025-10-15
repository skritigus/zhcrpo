// src/components/Groups/StudentOptionContent.jsx
import React from 'react';
import '../../styles/StudentOptionContent.css'; // Создадим этот CSS файл

const StudentOptionContent = ({ student }) => {
    if (!student) {
        return <span className="student-option-placeholder">Выберите студента...</span>;
    }

    return (
        <div className="student-option-content-wrapper">
            <div className="student-main-info">
                <span className="student-name">{student.name}</span>
            </div>
            {student.phoneNumber && (
                <div className="student-phone-number">
                    Тел: {student.phoneNumber}
                </div>
            )}
        </div>
    );
};

export default StudentOptionContent;