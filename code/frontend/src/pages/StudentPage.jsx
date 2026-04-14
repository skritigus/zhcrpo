import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { getMyStudentInfo } from '../services/api';
import ScheduleGrid from '../components/Schedule/ScheduleGrid';
import '../styles/Dashboard.css';

const StudentPage = () => {
    const { user } = useAuth();
    const [studentData, setStudentData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchInfo = async () => {
            try {
                const data = await getMyStudentInfo();
                setStudentData(data);
            } catch (err) {
                setError("Не удалось загрузить данные студента. Обратитесь к администратору для проверки вашего аккаунта.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchInfo();
    }, []);

    if (loading) return (
        <div className="dashboard-container">
            <div className="loading-state">
                <div className="loader"></div>
                <p>Загрузка данных вашего профиля...</p>
            </div>
        </div>
    );

    if (error) return (
        <div className="dashboard-container">
            <div className="error-state">
                <div className="error-icon">⚠️</div>
                <h2>Ошибка доступа</h2>
                <p>{error}</p>
                <button className="btn btn-primary" onClick={() => window.location.reload()}>Попробовать снова</button>
            </div>
        </div>
    );

    const scheduleItems = studentData?.groups?.flatMap(group => 
        (group.scheduleItems || []).map(item => ({...item, group}))
    ) || [];

    return (
        <div className="dashboard-container">
            <div className="dashboard-hero">
                <div className="hero-content">
                    <p className="hero-badge">Личный кабинет студента</p>
                    <h1>Привет, {user?.name}!</h1>
                    <p>Добро пожаловать в вашу танцевальную школу. Здесь ваше расписание и группы.</p>
                </div>
            </div>

            <div className="stat-grid">
                <div className="stat-card">
                    <div className="stat-label">Ваши группы</div>
                    <div className="stat-value">{studentData?.groups?.length || 0}</div>
                </div>
                <div className="stat-card">
                    <div className="stat-label">Занятий в неделю</div>
                    <div className="stat-value">{scheduleItems.length}</div>
                </div>
                <div className="stat-card">
                    <div className="stat-label">Телефон</div>
                    <div className="stat-value" style={{fontSize: '1.2rem'}}>{user?.phoneNumber}</div>
                </div>
            </div>

            <div className="dashboard-section">
                <div className="section-header">
                    <h2 className="section-title">Ваше расписание</h2>
                </div>
                <div className="glass-panel">
                    <ScheduleGrid 
                        scheduleItems={scheduleItems} 
                        halls={[]} 
                        isAdmin={false}
                    />
                </div>
            </div>

            <div className="dashboard-section">
                <div className="section-header">
                    <h2 className="section-title">Ваши группы</h2>
                </div>
                <div className="group-grid">
                    {(studentData?.groups || []).map(group => (
                        <div key={group.id} className="modern-card">
                            <div className="card-tag difficulty">{group.difficulty}</div>
                            <h3 style={{margin: '0 0 1rem 0'}}>Группа #{group.id}</h3>
                            
                            <div style={{display: 'flex', alignItems: 'center', marginTop: 'auto'}}>
                                <div className="trainer-avatar">
                                    {(group.trainer?.name || 'T')[0]}
                                </div>
                                <div>
                                    <div style={{fontSize: '0.9rem', fontWeight: 700}}>{group.trainer?.name || 'Тренер не назначен'}</div>
                                    <div style={{fontSize: '0.8rem', color: '#64748b'}}>{group.trainer?.danceStyle || 'Стиль не указан'}</div>
                                </div>
                            </div>
                        </div>
                    ))}
                    {(!studentData?.groups || studentData.groups.length === 0) && (
                        <div className="glass-panel" style={{gridColumn: '1 / -1', textAlign: 'center', padding: '3rem'}}>
                            <p style={{color: '#64748b', fontSize: '1.1rem'}}>Вы пока не записаны ни в одну группу. Обратитесь к администратору!</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default StudentPage;
