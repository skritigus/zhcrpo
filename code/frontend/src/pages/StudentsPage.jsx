// src/pages/StudentsPage.jsx
import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
    // getAllStudents, // Получаем из App.js
    createStudent,
    updateStudent,
    deleteStudent
} from '../services/api';
import StudentItem from '../components/Students/StudentItem';
import CreateStudentModal from '../components/Students/CreateStudentModal';
import '../styles/StudentsPage.css'; // Создадим этот CSS

const StudentsPage = ({ showAppNotification, appAllStudents, isLoadingAppLevelData, onMajorDataChange }) => {
    const [students, setStudents] = useState(appAllStudents || []);
    const [isLoading, setIsLoading] = useState(isLoadingAppLevelData);
    const [error, setError] = useState(null); // Для ошибок специфичных для этой страницы, если будут

    const [showCreateModal, setShowCreateModal] = useState(false);
    const [editingStudent, setEditingStudent] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [isProcessing, setIsProcessing] = useState(false);

    useEffect(() => {
        // console.log("StudentsPage: appAllStudents prop changed", appAllStudents);
        if (!isLoadingAppLevelData) {
            setStudents(appAllStudents || []);
            setIsLoading(false);
        } else {
            setIsLoading(true);
        }
    }, [appAllStudents, isLoadingAppLevelData]);

    const displayNotification = useCallback((...args) => {
        if (showAppNotification) showAppNotification(...args);
    }, [showAppNotification]);

    const handleOpenCreateModal = () => {
        setEditingStudent(null);
        setShowCreateModal(true);
    };

    const handleOpenEditModal = (student) => {
        setEditingStudent(student);
        setShowCreateModal(true);
    };

    const handleCloseModal = () => {
        setShowCreateModal(false);
        setEditingStudent(null);
    };

    const handleSubmitStudent = async (studentData) => {
        setIsProcessing(true);
        try {
            if (editingStudent && editingStudent.id) {
                await updateStudent(editingStudent.id, studentData);
                displayNotification("Студент успешно обновлен!", "success");
            } else {
                await createStudent(studentData);
                displayNotification("Студент успешно создан!", "success");
            }
            handleCloseModal();
            if (onMajorDataChange) onMajorDataChange();
        } catch (err) {
            console.error("StudentsPage: Failed to save student:", err);
            displayNotification(`Ошибка сохранения студента: ${err.message || 'Неизвестная ошибка'}`, 'error');
        } finally {
            setIsProcessing(false);
        }
    };

    const handleDeleteStudent = async (studentId) => {
        if (window.confirm(`Вы уверены, что хотите удалить студента?`)) {
            setIsProcessing(true);
            try {
                await deleteStudent(studentId);
                displayNotification("Студент успешно удален.", "success");
                if (onMajorDataChange) onMajorDataChange();
            } catch (err) {
                console.error("StudentsPage: Failed to delete student:", err);
                displayNotification(`Ошибка удаления студента: ${err.message || 'Неизвестная ошибка'}`, 'error');
            } finally {
                setIsProcessing(false);
            }
        }
    };

    const filteredStudents = useMemo(() => {
        const studentsToFilter = students || [];
        if (!searchTerm.trim()) {
            return studentsToFilter;
        }
        const searchTermLower = searchTerm.toLowerCase();
        return studentsToFilter.filter(student =>
            (student.name?.toLowerCase() || '').includes(searchTermLower) ||
            (student.phoneNumber?.toLowerCase() || '').includes(searchTermLower)
        );
    }, [students, searchTerm]);

    if (isLoading && (!students || students.length === 0) ) {
        return <div className="loading-message">Загрузка списка студентов...</div>;
    }
    // Глобальная ошибка загрузки из App.js обрабатывается там же
    // if (error) {
    //     return <div className="error-message">Ошибка загрузки студентов: {error}</div>;
    // }

    return (
        <div className="students-page-container">
            <div className="page-header">
                <h1>Студенты</h1>
                <button className="btn btn-primary" onClick={handleOpenCreateModal} disabled={isProcessing}>
                    {isProcessing ? 'Обработка...' : 'Добавить студента'}
                </button>
            </div>

            <div className="search-bar-container">
                <input
                    type="text"
                    placeholder="Поиск студентов (имя, телефон...)"
                    className="students-search-input"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>

            {filteredStudents.length > 0 ? (
                <div className="students-list-container"> {/* Изменил класс для возможной grid-верстки */}
                    {filteredStudents.map(student => (
                        <StudentItem
                            key={student.id}
                            student={student}
                            onEdit={handleOpenEditModal}
                            onDelete={handleDeleteStudent}
                        />
                    ))}
                </div>
            ) : (
                <p className="no-items-message">
                    {searchTerm.trim() ? "Студенты не найдены по вашему запросу." : "Нет доступных студентов."}
                </p>
            )}

            <CreateStudentModal
                show={showCreateModal}
                onClose={handleCloseModal}
                onSubmit={handleSubmitStudent}
                existingStudentData={editingStudent}
            />
        </div>
    );
};

export default StudentsPage;