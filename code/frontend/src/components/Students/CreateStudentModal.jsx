// src/components/Students/CreateStudentModal.jsx
import React, { useState, useEffect } from 'react';
import '../../styles/CreateStudentModal.css'; // Создадим этот CSS

const CreateStudentModal = ({ show, onClose, onSubmit, existingStudentData = null }) => {
    const [name, setName] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [error, setError] = useState('');

    const isEditMode = !!existingStudentData;

    useEffect(() => {
        if (show) {
            setError('');
            if (isEditMode && existingStudentData) {
                setName(existingStudentData.name || '');
                setPhoneNumber(existingStudentData.phoneNumber || '');
            } else {
                setName('');
                setPhoneNumber('');
            }
        }
    }, [show, existingStudentData, isEditMode]);

    if (!show) {
        return null;
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!name.trim() || !phoneNumber.trim()) {
            setError("Пожалуйста, заполните имя и номер телефона.");
            return;
        }
        setError('');
        const studentData = { name, phoneNumber };
        const submissionData = isEditMode ? { id: existingStudentData.id, ...studentData } : studentData;
        onSubmit(submissionData);
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content create-student-modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close-btn" onClick={onClose}>×</button>
                <h2>{isEditMode ? "Редактировать студента" : "Добавить нового студента"}</h2>
                {error && <p className="modal-error-text">{error}</p>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="studentName">Имя:</label>
                        <input
                            type="text"
                            id="studentName"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="studentPhoneNumber">Номер телефона:</label>
                        <input
                            type="text"
                            id="studentPhoneNumber"
                            value={phoneNumber}
                            onChange={(e) => setPhoneNumber(e.target.value)}
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

export default CreateStudentModal;