// src/components/Groups/CreateGroupModal.jsx
import React, { useState, useEffect, useRef, useMemo } from 'react';
import '../../styles/CreateGroupModal.css';
import StudentOptionContent from './StudentOptionContent';

const CreateGroupModal = ({
                              show,
                              onClose,
                              onSubmit, // Эта функция вызывается здесь, но определена в GroupsPage
                              allTrainers = [],
                              allStudents = [],
                              initialData = null
                          }) => {
    // ... (все хуки useState, useRef, useMemo, useEffect в начале) ...
    const [difficulty, setDifficulty] = useState('');
    const [selectedTrainer, setSelectedTrainer] = useState(null);
    const [trainerSearchTerm, setTrainerSearchTerm] = useState('');
    const [isTrainerDropdownOpen, setIsTrainerDropdownOpen] = useState(false);
    const trainerDropdownRef = useRef(null);

    const [currentStudentsInGroup, setCurrentStudentsInGroup] = useState([]);
    const [isStudentDropdownOpen, setIsStudentDropdownOpen] = useState(false);
    const [selectedStudentForAction, setSelectedStudentForAction] = useState(null);
    const [studentSearchTerm, setStudentSearchTerm] = useState('');
    const studentDropdownRef = useRef(null);

    const difficultyOptions = [
        { value: 'Beg', label: 'Beg' },
        { value: 'Mid', label: 'Mid' },
        { value: 'Pro', label: 'Pro' },
    ];

    const studentsInGroupIds = useMemo(() => new Set(currentStudentsInGroup.map(s => s.id)), [currentStudentsInGroup]);

    const availableStudentsForDropdown = useMemo(() => {
        return allStudents.filter(s =>
            s &&
            !studentsInGroupIds.has(s.id) &&
            (s.name && s.name.toLowerCase().includes(studentSearchTerm.toLowerCase()) ||
                (s.phoneNumber && s.phoneNumber.includes(studentSearchTerm)))
        );
    }, [allStudents, studentsInGroupIds, studentSearchTerm]);

    const filteredTrainers = useMemo(() => {
        return allTrainers.filter(trainer =>
            trainer &&
            trainer.name &&
            (trainer.name.toLowerCase().includes(trainerSearchTerm.toLowerCase()) ||
                (trainer.danceStyle && trainer.danceStyle.toLowerCase().includes(trainerSearchTerm.toLowerCase())))
        );
    }, [allTrainers, trainerSearchTerm]);

    useEffect(() => {
        if (show) {
            if (initialData) {
                setDifficulty(initialData.difficulty || '');
                const trainerObj = allTrainers.find(t => t && initialData.trainer && t.id === initialData.trainer.id);
                setSelectedTrainer(trainerObj || null);
                setCurrentStudentsInGroup(initialData.students || []);
            } else {
                setDifficulty('');
                setSelectedTrainer(null);
                setCurrentStudentsInGroup([]);
            }
            setTrainerSearchTerm('');
            setIsTrainerDropdownOpen(false);
            setStudentSearchTerm('');
            setIsStudentDropdownOpen(false);
            setSelectedStudentForAction(null);
        }
    }, [initialData, show, allTrainers]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (trainerDropdownRef.current && !trainerDropdownRef.current.contains(event.target)) {
                setIsTrainerDropdownOpen(false);
            }
        };
        if (isTrainerDropdownOpen) document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [isTrainerDropdownOpen]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (studentDropdownRef.current && !studentDropdownRef.current.contains(event.target)) {
                setIsStudentDropdownOpen(false);
            }
        };
        if (isStudentDropdownOpen) document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [isStudentDropdownOpen]);

    if (!show) {
        return null;
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!difficulty || !selectedTrainer) {
            alert("Пожалуйста, выберите уровень сложности и тренера.");
            return;
        }
        // Формируем данные в соответствии с ожидаемым форматом бэкенда
        const groupDataToSend = {
            difficulty,
            trainerId: selectedTrainer.id, // <--- ИЗМЕНЕНО: отправляем trainerId
            studentsId: currentStudentsInGroup.map(s => s.id),
            // scheduleItemsId: initialData?.scheduleItemsId || [] // Если бэкенд не ожидает это при создании, можно убрать
        };
        // Если scheduleItemsId не нужно отправлять при создании новой группы,
        // а только при редактировании, то можно добавить условие:
        // if (initialData && initialData.scheduleItemsId) {
        //   groupDataToSend.scheduleItemsId = initialData.scheduleItemsId;
        // }

        console.log("Данные для создания группы:", JSON.stringify(groupDataToSend, null, 2)); // Логируем отправляемые данные
        onSubmit(groupDataToSend);
    };

    const handleDifficultySelect = (selectedDiff) => setDifficulty(selectedDiff);
    const handleTrainerSelect = (trainer) => {
        setSelectedTrainer(trainer);
        setTrainerSearchTerm('');
        setIsTrainerDropdownOpen(false);
    };

    const handleStudentSelectForAction = (student) => {
        setSelectedStudentForAction(student);
        setStudentSearchTerm('');
        setIsStudentDropdownOpen(false);
    };

    const handleAddStudentToGroup = () => {
        if (!selectedStudentForAction) return;
        if (!studentsInGroupIds.has(selectedStudentForAction.id)) {
            setCurrentStudentsInGroup(prev => [...prev, selectedStudentForAction]);
        }
        setSelectedStudentForAction(null);
    };

    const handleRemoveStudentFromGroup = (studentIdToRemove) => {
        setCurrentStudentsInGroup(prev => prev.filter(s => s.id !== studentIdToRemove));
    };

    // JSX разметка (без изменений, приведена для полноты)
    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content create-group-modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close-btn" onClick={onClose}>×</button>
                <h2>{initialData ? "Редактировать группу" : "Создать новую группу"}</h2>
                <form onSubmit={handleSubmit}>
                    {/* Уровень сложности */}
                    <div className="form-group">
                        <label>Уровень сложности:</label>
                        <div className="difficulty-buttons-container">
                            {difficultyOptions.map(option => (
                                <button
                                    key={option.value}
                                    type="button"
                                    className={`difficulty-btn ${difficulty === option.value ? `selected ${option.value.toLowerCase()}` : 'default'}`}
                                    onClick={() => handleDifficultySelect(option.value)}
                                >
                                    {option.label}
                                </button>
                            ))}
                        </div>
                    </div>

                    {/* Выбор тренера (кастомный селект) */}
                    <div className="form-group">
                        <label>Тренер:</label>
                        <div className="custom-select-wrapper" ref={trainerDropdownRef}>
                            <div
                                className={`custom-select-input ${isTrainerDropdownOpen ? 'focused' : ''}`}
                                onClick={() => setIsTrainerDropdownOpen(prev => !prev)}
                                tabIndex={0}
                            >
                                {selectedTrainer ? (
                                    <div className="trainer-option-display">
                                        <span className="trainer-name">{selectedTrainer.name || 'Имя не указано'}</span>
                                        {selectedTrainer.danceStyle && <span className="trainer-style">{selectedTrainer.danceStyle}</span>}
                                    </div>
                                ) : (
                                    <span className="custom-select-placeholder">Выберите тренера...</span>
                                )}
                                <span className={`custom-select-arrow ${isTrainerDropdownOpen ? 'open' : ''}`}>▼</span>
                            </div>
                            {isTrainerDropdownOpen && (
                                <div className="custom-select-dropdown">
                                    <input
                                        type="text"
                                        className="custom-select-search"
                                        placeholder="Поиск (имя, стиль)..."
                                        value={trainerSearchTerm}
                                        onChange={(e) => setTrainerSearchTerm(e.target.value)}
                                        onClick={(e) => e.stopPropagation()}
                                        autoFocus
                                    />
                                    <div className="custom-select-options">
                                        {filteredTrainers.length > 0 ? (
                                            filteredTrainers.map(trainer => (
                                                <div
                                                    key={trainer.id}
                                                    className="custom-select-option trainer-option-item"
                                                    onClick={() => handleTrainerSelect(trainer)}
                                                >
                                                    <div className="trainer-option-display">
                                                        <span className="trainer-name">{trainer.name || 'Имя не указано'}</span>
                                                        {trainer.danceStyle && <span className="trainer-style">{trainer.danceStyle}</span>}
                                                    </div>
                                                </div>
                                            ))
                                        ) : (
                                            <div className="custom-select-no-options">Тренеры не найдены</div>
                                        )}
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Раздел управления студентами */}
                    <div className="form-group students-management-section">
                        <label>Студенты в группе ({currentStudentsInGroup.length}):</label>
                        {currentStudentsInGroup.length > 0 ? (
                            <ul className="students-list-modal">
                                {currentStudentsInGroup.map(student => (
                                    <li key={student.id}>
                                        <span>{student.name} (Тел: {student.phoneNumber || 'N/A'})</span>
                                        <button
                                            type="button"
                                            className="btn-remove-student-modal"
                                            onClick={() => handleRemoveStudentFromGroup(student.id)}
                                        >
                                            Удалить
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p className="no-students-message">В группе пока нет студентов.</p>
                        )}

                        <div className="add-student-form-modal">
                            <label style={{marginTop: '15px', marginBottom: '5px'}}>Добавить студента:</label>
                            <div className="custom-select-wrapper student-select-modal" ref={studentDropdownRef}>
                                <div
                                    className={`custom-select-input ${isStudentDropdownOpen ? 'focused' : ''}`}
                                    onClick={() => setIsStudentDropdownOpen(prev => !prev)}
                                    tabIndex={0}
                                >
                                    {selectedStudentForAction ? (
                                        <StudentOptionContent student={selectedStudentForAction} />
                                    ) : (
                                        <span className="custom-select-placeholder">Выберите студента...</span>
                                    )}
                                    <span className={`custom-select-arrow ${isStudentDropdownOpen ? 'open' : ''}`}>▼</span>
                                </div>
                                {isStudentDropdownOpen && (
                                    <div className="custom-select-dropdown">
                                        <input
                                            type="text"
                                            className="custom-select-search"
                                            placeholder="Поиск (имя, телефон)..."
                                            value={studentSearchTerm}
                                            onChange={(e) => setStudentSearchTerm(e.target.value)}
                                            onClick={(e) => e.stopPropagation()}
                                            autoFocus
                                        />
                                        <div className="custom-select-options">
                                            {availableStudentsForDropdown.length > 0 ? (
                                                availableStudentsForDropdown.map(student => (
                                                    student &&
                                                    <div
                                                        key={student.id}
                                                        className="custom-select-option student-option-item"
                                                        onClick={() => handleStudentSelectForAction(student)}
                                                    >
                                                        <StudentOptionContent student={student} />
                                                    </div>
                                                ))
                                            ) : (
                                                <div className="custom-select-no-options">Студенты не найдены или уже в группе</div>
                                            )}
                                        </div>
                                    </div>
                                )}
                            </div>
                            <button
                                type="button"
                                className="btn btn-add-student-modal"
                                onClick={handleAddStudentToGroup}
                                disabled={!selectedStudentForAction}
                            >
                                Добавить выбранного студента
                            </button>
                        </div>
                    </div>

                    <div className="modal-actions">
                        <button type="button" className="btn" onClick={onClose} style={{marginRight: '10px'}}>Отмена</button>
                        <button type="submit" className="btn btn-primary">
                            {initialData ? "Сохранить изменения" : "Создать"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateGroupModal;