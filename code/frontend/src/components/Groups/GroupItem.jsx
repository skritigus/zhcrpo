// src/components/Groups/GroupItem.jsx
// Добавляем useCallback в импорт из React
import React, { useState, useEffect, useRef, useMemo, useCallback } from 'react';
import { getGroupById, updateGroup, getScheduleItemsByGroupId } from '../../services/api';
import '../../styles/GroupItem.css';
import StudentOptionContent from './StudentOptionContent';
import GroupOptionContent from '../Schedule/GroupOptionContent';


const GroupItem = ({
                       group: initialGroup,
                       allStudents,
                       onForceRefreshList,
                       showNotification,
                       onDeleteGroup,
                       isCurrentlyExpanded,
                       onToggle
                   }) => {
    const [detailedGroupData, setDetailedGroupData] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const [isStudentDropdownOpen, setIsStudentDropdownOpen] = useState(false);
    const [selectedStudentForAction, setSelectedStudentForAction] = useState(null);
    const [studentSearchTerm, setStudentSearchTerm] = useState('');
    const studentDropdownRef = useRef(null);
    const detailsContentRef = useRef(null);
    const prevGroupIdRef = useRef(initialGroup.id);

    useEffect(() => {
        const currentId = initialGroup.id;
        if (prevGroupIdRef.current !== null && prevGroupIdRef.current !== currentId) {
            setDetailedGroupData(null);
            setIsLoading(false);
            // isCurrentlyExpanded будет управляться извне, так что здесь его не трогаем
            // Если нужно принудительно свернуть при смене ID, то onToggle(prevGroupIdRef.current) если он был expanded
            // Но GroupsPage должна сама свернуть старый элемент
        } else if (prevGroupIdRef.current === currentId && detailedGroupData) {
            setDetailedGroupData(prev => ({
                ...prev,
                group: { ...(prev ? prev.group : {}), ...initialGroup }
            }));
        }
        prevGroupIdRef.current = currentId;
    }, [initialGroup, detailedGroupData]); // detailedGroupData добавлено, чтобы изменения в initialGroup отражались в уже загруженном detailedGroupData

    useEffect(() => {
        if (!isCurrentlyExpanded) {
            setSelectedStudentForAction(null);
            setStudentSearchTerm('');
            setIsStudentDropdownOpen(false);
        }
    }, [isCurrentlyExpanded]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (studentDropdownRef.current && !studentDropdownRef.current.contains(event.target)) {
                setIsStudentDropdownOpen(false);
            }
        };
        if (isStudentDropdownOpen) document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [isStudentDropdownOpen]);

    const loadFullDetails = useCallback(async (forceReload = false) => {
        const groupId = initialGroup.id;
        if (detailedGroupData && detailedGroupData.group.id === groupId && !forceReload) {
            return;
        }

        setIsLoading(true);
        try {
            const groupFromServer = await getGroupById(groupId);
            let scheduleDetailsForGroup = [];

            try {
                scheduleDetailsForGroup = await getScheduleItemsByGroupId(groupId);
            } catch (e) {
                console.warn(`GroupItem ID ${groupId}: Ошибка загрузки расписания группы:`, e.message);
                showNotification(`Ошибка загрузки расписания группы: ${e.message}`, 'warning');
            }

            setDetailedGroupData({
                group: { ...initialGroup, ...groupFromServer },
                scheduleDetails: scheduleDetailsForGroup
            });
        } catch (error) {
            console.error(`GroupItem ID ${groupId}: Error in loadFullDetails:`, error);
            showNotification(`Ошибка загрузки данных для группы: ${error.message}`, 'error');
            setDetailedGroupData(null);
        } finally {
            setIsLoading(false);
        }
    }, [initialGroup, showNotification, detailedGroupData]);


    useEffect(() => {
        if (isCurrentlyExpanded) {
            if (!detailedGroupData || detailedGroupData.group.id !== initialGroup.id) {
                loadFullDetails();
            }
        }
    }, [isCurrentlyExpanded, initialGroup.id, detailedGroupData, loadFullDetails]);

    const handleStudentChange = async () => {
        if (isCurrentlyExpanded) {
            await loadFullDetails(true);
        } else {
            setDetailedGroupData(null);
        }
        if (onForceRefreshList) {
            onForceRefreshList();
        }
    };

    const handleAddStudent = async () => {
        if (!selectedStudentForAction || !detailedGroupData) return;
        const studentIdNum = parseInt(selectedStudentForAction.id, 10);
        const currentStudents = Array.isArray(detailedGroupData.group.students) ? detailedGroupData.group.students : [];
        if (currentStudents.some(s => s.id === studentIdNum)) {
            showNotification("Студент уже в этой группе.", "warning"); return;
        }
        const updatedStudentIds = [...currentStudents.map(s => s.id), studentIdNum];
        const payload = {
            difficulty: detailedGroupData.group.difficulty,
            trainerId: detailedGroupData.group.trainer.id,
            studentsId: updatedStudentIds
        };
        try {
            await updateGroup(detailedGroupData.group.id, payload);
            showNotification("Студент успешно добавлен.", "success");
            setSelectedStudentForAction(null); setStudentSearchTerm('');
            await handleStudentChange();
        } catch (error) { showNotification(`Ошибка добавления студента: ${error.message}`, 'error'); }
    };

    const handleRemoveStudent = async (studentIdToRemove) => {
        if (!detailedGroupData) return;
        if (!window.confirm(`Удалить студента из группы?`)) return;
        const updatedStudentIds = (detailedGroupData.group.students || []).filter(s => s.id !== studentIdToRemove).map(s => s.id);
        const payload = {
            difficulty: detailedGroupData.group.difficulty,
            trainerId: detailedGroupData.group.trainer.id,
            studentsId: updatedStudentIds
        };
        try {
            await updateGroup(detailedGroupData.group.id, payload);
            showNotification("Студент успешно удален.", "success");
            await handleStudentChange();
        } catch (error) { showNotification(`Ошибка удаления студента: ${error.message}`, 'error'); }
    };

    const studentsInGroupIds = useMemo(() => new Set((detailedGroupData?.group.students || []).map(s => s.id)), [detailedGroupData?.group.students]);
    const availableStudentsForDropdown = useMemo(() => {
        if (!allStudents) return [];
        return allStudents.filter(s =>
            s && !studentsInGroupIds.has(s.id) &&
            ((s.name && s.name.toLowerCase().includes(studentSearchTerm.toLowerCase())) ||
                (s.phoneNumber && s.phoneNumber.includes(studentSearchTerm)))
        );
    }, [allStudents, studentsInGroupIds, studentSearchTerm]);

    const handleStudentSelect = (student) => {
        setSelectedStudentForAction(student);
        setStudentSearchTerm('');
        setIsStudentDropdownOpen(false);
    };

    const formatTime = (timeStr) => timeStr && typeof timeStr === 'string' ? timeStr.substring(0, 5) : 'N/A';
    const formatDay = (dayStr) => {
        if (!dayStr || typeof dayStr !== 'string') return '';
        const daysMap = { "Monday": "ПН", "Tuesday": "ВТ", "Wednesday": "СР", "Thursday": "ЧТ", "Friday": "ПТ", "Saturday": "СБ", "Sunday": "ВС" };
        return daysMap[dayStr] || dayStr;
    };

    const displayGroupInHeader = detailedGroupData?.group || initialGroup;

    return (
        <div className={`group-item-wrapper ${isCurrentlyExpanded && detailedGroupData && !isLoading ? 'expanded' : ''}`}>
            <div className="group-item-header" onClick={onToggle}>
                <div className="group-header-content-wrapper">
                    <GroupOptionContent item={{ group: displayGroupInHeader }} />
                </div>
                {isLoading && isCurrentlyExpanded ? (
                    <span className="loading-spinner-icon">⏳</span>
                ) : (
                    <span className={`expand-icon ${isCurrentlyExpanded ? 'expanded' : ''}`}>▼</span>
                )}
            </div>

            <div
                ref={detailsContentRef}
                className="group-item-details-collapsible"
                style={{
                    maxHeight: isCurrentlyExpanded && detailedGroupData && !isLoading ? `${detailsContentRef.current?.scrollHeight}px` : '0px',
                }}
            >
                {isLoading && isCurrentlyExpanded && (!detailedGroupData || detailedGroupData.group.id !== initialGroup.id) && (
                    <div className="group-item-details-inner-loading">
                        <p>Загрузка данных группы...</p>
                    </div>
                )}
                {!isLoading && detailedGroupData && detailedGroupData.group.id === initialGroup.id && (
                    <div className="group-item-details-inner-content">
                        <>
                            <h3>Расписание занятий:</h3>
                            {(detailedGroupData.group.scheduleItemsId && detailedGroupData.group.scheduleItemsId.length > 0) ? (
                                detailedGroupData.scheduleDetails && detailedGroupData.scheduleDetails.length > 0 ? (
                                    <ul className="schedule-list">
                                        {detailedGroupData.scheduleDetails.map((item) => (
                                            <li key={item.id}>
                                                {item.error ? (
                                                    <span className="schedule-item-error">
                                                        Занятие (ошибка загрузки)
                                                    </span>
                                                ) : (
                                                    <>
                                                        {`${formatDay(item.dayOfWeek)}: `}
                                                        {formatTime(item.startTime)} - {formatTime(item.endTime)}
                                                        {item.hall && item.hall.name && ` (Зал: ${item.hall.name})`}
                                                    </>
                                                )}
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p>Информация о расписании отсутствует или не удалось загрузить.</p>
                                )
                            ) : (
                                <p>Расписание не задано.</p>
                            )}

                            <h3>Тренер:</h3>
                            {detailedGroupData.group.trainer ? ( <p> {detailedGroupData.group.trainer.name} ({detailedGroupData.group.trainer.danceStyle || 'N/A'}), Тел: {detailedGroupData.group.trainer.phoneNumber || 'N/A'} </p> ) : ( <p>Тренер не назначен.</p> )}
                            <div className="student-actions-container">
                                <h4>Добавить студента:</h4>
                                <div className="custom-select-group student-select-group" ref={studentDropdownRef}>
                                    <div className={`custom-select-input ${isStudentDropdownOpen ? 'focused' : ''}`} onClick={() => setIsStudentDropdownOpen(prev => !prev)} tabIndex={0} onKeyDown={e => (e.key === 'Enter' || e.key === ' ') && setIsStudentDropdownOpen(prev => !prev)} > {selectedStudentForAction ? ( <StudentOptionContent student={selectedStudentForAction} /> ) : ( <span className="custom-select-placeholder">Выберите студента...</span> )} <span className={`custom-select-arrow ${isStudentDropdownOpen ? 'open' : ''}`}>▼</span> </div> {isStudentDropdownOpen && ( <div className="custom-select-dropdown"> <input type="text" className="custom-select-search" placeholder="Поиск (имя, телефон)..." value={studentSearchTerm} onChange={(e) => setStudentSearchTerm(e.target.value)} onClick={(e) => e.stopPropagation()} autoFocus /> <div className="custom-select-options"> {availableStudentsForDropdown.length > 0 ? ( availableStudentsForDropdown.map(student => ( student && <div key={student.id} className="custom-select-option student-option-item" onClick={() => handleStudentSelect(student)} tabIndex={0} onKeyDown={e => e.key === 'Enter' && handleStudentSelect(student)} > <StudentOptionContent student={student} /> </div> )) ) : ( <div className="custom-select-no-options">Студенты не найдены или уже в группе</div> )} </div> </div> )} </div> <button className="btn btn-primary btn-sm add-student-button" onClick={handleAddStudent} disabled={!selectedStudentForAction} > Добавить </button>
                            </div>
                            <h3>Студенты ({detailedGroupData.group.students ? detailedGroupData.group.students.length : 0}):</h3>
                            {detailedGroupData.group.students && detailedGroupData.group.students.length > 0 ? ( <ul className="students-list"> {detailedGroupData.group.students.map(student => ( <li key={student.id}> <span>{student.name} (Тел: {student.phoneNumber || 'N/A'})</span> <button className="btn btn-danger btn-sm" onClick={() => handleRemoveStudent(student.id)} > Удалить </button> </li> ))} </ul> ) : ( <p>В группе нет студентов.</p> )}
                            <div className="group-actions-container"> <button className="btn btn-danger" onClick={() => onDeleteGroup(detailedGroupData.group.id)} > Удалить всю группу </button> </div>
                        </>
                    </div>
                )}
                {!isLoading && isCurrentlyExpanded && !detailedGroupData && (
                    <div className="group-item-details-inner-content">
                        <p>Не удалось загрузить данные. Попробуйте снова.</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default GroupItem;