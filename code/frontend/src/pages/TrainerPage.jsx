import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { getMyTrainerInfo } from '../services/api';
import ScheduleGrid from '../components/Schedule/ScheduleGrid';
import '../styles/Dashboard.css';

const TrainerPage = () => {
    const { user } = useAuth();
    const [trainerData, setTrainerData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchInfo = async () => {
            try {
                const data = await getMyTrainerInfo();
                setTrainerData(data);
            } catch (err) {
                setError("Не удалось загрузить данные тренера. Убедитесь, что у вас есть роль тренера и ваш профиль настроен.");
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
                <p>Загрузка панели управления тренера...</p>
            </div>
        </div>
    );

    if (error) return (
        <div className="dashboard-container">
            <div className="error-state">
                <div className="error-icon">👨‍🏫</div>
                <h2>Ошибка доступа</h2>
                <p>{error}</p>
                <button className="btn btn-primary" onClick={() => window.location.reload()}>Попробовать снова</button>
            </div>
        </div>
    );

    const scheduleItems = trainerData?.groups?.flatMap(group => 
        (group.scheduleItems || []).map(item => ({...item, group}))
    ) || [];

    return (
        <div className="dashboard-container">
            <div className="dashboard-hero" style={{background: 'linear-gradient(135deg, #10b981 0%, #3b82f6 100%)'}}>
                <div className="hero-content">
                    <p className="hero-badge">Панель управления тренера</p>
                    <h1>Здравствуйте, {user?.name}!</h1>
                    <p>Ваше расписание занятий и список групп с контактными данными студентов.</p>
                </div>
            </div>

            <div className="stat-grid">
                <div className="stat-card">
                    <div className="stat-label">Ваш стиль</div>
                    <div className="stat-value" style={{fontSize: '1.5rem'}}>{trainerData?.danceStyle || 'Не указан'}</div>
                </div>
                <div className="stat-card">
                    <div className="stat-label">Групп в управлении</div>
                    <div className="stat-value">{trainerData?.groups?.length || 0}</div>
                </div>
                <div className="stat-card">
                    <div className="stat-label">Занятий в неделю</div>
                    <div className="stat-value">{scheduleItems.length}</div>
                </div>
            </div>

            <div className="dashboard-section">
                <div className="section-header">
                    <h2 className="section-title">Расписание тренировок</h2>
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
                    <h2 className="section-title">Ваши группы и студенты</h2>
                </div>
                <div className="group-grid">
                    {(trainerData?.groups || []).map(group => (
                        <div key={group.id} className="modern-card">
                            <div className="card-tag difficulty">{group.difficulty}</div>
                            <h3 style={{margin: '0 0 1rem 0'}}>Группа #{group.id}</h3>
                            
                            <div className="student-list" style={{marginTop: '1.5rem'}}>
                                <div style={{fontSize: '0.8rem', fontWeight: 600, color: '#64748b', marginBottom: '0.5rem'}}>СПИСОК СТУДЕНТОВ ({group.students?.length || 0})</div>
                                {(group.students || []).map(student => (
                                    <div key={student.id} className="student-list-item">
                                        <div className="trainer-avatar" style={{width: '32px', height: '32px', fontSize: '0.8rem'}}>
                                            {student.name[0]}
                                        </div>
                                        <div>
                                            <div style={{fontSize: '0.9rem', fontWeight: 600}}>{student.name}</div>
                                            <div style={{fontSize: '0.8rem', color: '#64748b'}}>{student.phoneNumber}</div>
                                        </div>
                                    </div>
                                ))}
                                {(!group.students || group.students.length === 0) && (
                                    <p style={{fontSize: '0.875rem', color: '#94a3b8', fontStyle: 'italic'}}>В группе пока нет студентов.</p>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default TrainerPage;
