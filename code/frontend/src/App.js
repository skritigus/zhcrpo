// App.js
import React, { useState, useEffect, useCallback, createContext, useRef } from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import Navbar from './components/Navbar';
import SchedulePage from './pages/SchedulePage';
import GroupsPage from './pages/GroupsPage';
import TrainersPage from './pages/TrainersPage';
import StudentsPage from './pages/StudentsPage';
import HallsPage from './pages/HallsPage';
import AddScheduleModal from './components/Schedule/AddScheduleModal';
import {
    getAllGroups,
    addScheduleItem,
    fetchHalls,
    fetchScheduleItems,
    deleteScheduleItem,
    updateScheduleItem,
    getAllStudents,
    getAllTrainers
} from './services/api';
import { TransitionGroup, CSSTransition } from 'react-transition-group';
import './styles/main.css';

// Создаем контекст для передачи данных и функций в AnimatedRoutes
const AppContext = createContext(null);

// Компонент-обертка для анимированных маршрутов
const AnimatedRoutes = () => {
    const location = useLocation();
    const nodeRefs = useRef({});

    const appContext = React.useContext(AppContext);
    if (!appContext) return null;

    const {
        halls, groups, scheduleItems, allStudents, allTrainers,
        isLoading: isLoadingAppLevelData,
        error: appError,
        handleOpenAddModal, handleOpenViewModal,
        showAppNotification, loadInitialData
    } = appContext; // Используем React.useContext

    if (isLoadingAppLevelData && !appError) {
        return <p style={{ textAlign: 'center', padding: '20px' }}>Загрузка основных данных приложения...</p>;
    }
    if (appError && !isLoadingAppLevelData) {
        return <p style={{ color: 'red', textAlign: 'center', padding: '20px' }}>{appError}</p>;
    }

    if (!nodeRefs.current[location.pathname]) {
        nodeRefs.current[location.pathname] = React.createRef(); // Используем React.createRef
    }
    const currentNodeRef = nodeRefs.current[location.pathname];

    return (
        <TransitionGroup component="div" className="route-section-wrapper">
            <CSSTransition
                key={location.pathname}
                nodeRef={currentNodeRef}
                timeout={600}
                classNames="page-transition"
                unmountOnExit
            >
                <div ref={currentNodeRef} className="page-container-for-transition">
                    <Routes location={location}>
                        <Route
                            path="/"
                            element={ <SchedulePage halls={halls} groups={groups} scheduleItems={scheduleItems} isLoadingAppLevelData={isLoadingAppLevelData} onAddSlotClick={handleOpenAddModal} onViewItemClick={handleOpenViewModal} onAddNewScheduleItemClick={() => handleOpenAddModal(null)} /> }
                        />
                        <Route
                            path="/schedule"
                            element={ <SchedulePage halls={halls} groups={groups} scheduleItems={scheduleItems} isLoadingAppLevelData={isLoadingAppLevelData} onAddSlotClick={handleOpenAddModal} onViewItemClick={handleOpenViewModal} onAddNewScheduleItemClick={() => handleOpenAddModal(null)} /> }
                        />
                        <Route
                            path="/groups"
                            element={ <GroupsPage appGroups={groups} appAllStudents={allStudents} appAllTrainers={allTrainers} isLoadingAppLevelData={isLoadingAppLevelData} showAppNotification={showAppNotification} onMajorDataChange={loadInitialData} /> }
                        />
                        <Route
                            path="/trainers"
                            element={ <TrainersPage appAllTrainers={allTrainers} isLoadingAppLevelData={isLoadingAppLevelData} showAppNotification={showAppNotification} onMajorDataChange={loadInitialData} /> }
                        />
                        <Route
                            path="/students"
                            element={ <StudentsPage appAllStudents={allStudents} isLoadingAppLevelData={isLoadingAppLevelData} showAppNotification={showAppNotification} onMajorDataChange={loadInitialData} /> }
                        />
                        <Route
                            path="/halls"
                            element={ <HallsPage appAllHalls={halls} isLoadingAppLevelData={isLoadingAppLevelData} showAppNotification={showAppNotification} onMajorDataChange={loadInitialData} /> }
                        />
                    </Routes>
                </div>
            </CSSTransition>
        </TransitionGroup>
    );
};


function App() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalInitialData, setModalInitialData] = useState(null);
    const [modalExistingItem, setModalExistingItem] = useState(null);

    const [groups, setGroups] = useState([]);
    const [halls, setHalls] = useState([]);
    const [scheduleItems, setScheduleItems] = useState([]);
    const [allStudents, setAllStudents] = useState([]);
    const [allTrainers, setAllTrainers] = useState([]);

    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null); // Общая ошибка загрузки
    const [notification, setNotification] = useState({ message: '', type: '', show: false });

    const showAppNotification = useCallback((message, type, duration = 3000) => {
        setNotification({ message, type, show: true });
        setTimeout(() => { setNotification({ message: '', type: '', show: false }); }, duration);
    }, []);

    const reloadScheduleItems = useCallback(async () => {
        try {
            const scheduleItemsData = await fetchScheduleItems();
            setScheduleItems(scheduleItemsData || []);
        } catch (err) {
            console.error("App.js: Failed to fetch schedule items:", err);
            showAppNotification("Ошибка обновления данных расписания.", "error", 4000);
        } finally {

        }
    }, [showAppNotification]);

    const loadInitialData = useCallback(async () => {
        // console.log("App.js: loadInitialData - STARTING APP-LEVEL DATA FETCH");
        setIsLoading(true);
        setError(null);
        let partialSuccess = false;
        try {
            const results = await Promise.allSettled([
                getAllGroups(), fetchHalls(), fetchScheduleItems(), getAllStudents(), getAllTrainers()
            ]);
            // console.log("App.js: loadInitialData - Promise.allSettled results:", results);
            if (results[0].status === 'fulfilled' && results[0].value) { setGroups(results[0].value); partialSuccess = true; }
            else { console.error("App.js: Failed to fetch groups:", results[0].reason); setGroups([]); showAppNotification("Ошибка загрузки списка групп.", "error", 4000); }
            if (results[1].status === 'fulfilled' && results[1].value) { setHalls(results[1].value); partialSuccess = true; }
            else { console.error("App.js: Failed to fetch halls:", results[1].reason); setHalls([]); showAppNotification("Ошибка загрузки списка залов.", "error", 4000); }
            if (results[2].status === 'fulfilled' && results[2].value) { setScheduleItems(results[2].value); partialSuccess = true; }
            else { console.error("App.js: Failed to fetch schedule items:", results[2].reason); setScheduleItems([]); showAppNotification("Ошибка загрузки расписания.", "error", 4000); }
            if (results[3].status === 'fulfilled' && results[3].value) { setAllStudents(results[3].value); partialSuccess = true; }
            else { console.error("App.js: Failed to fetch students:", results[3].reason); setAllStudents([]); showAppNotification("Ошибка загрузки списка студентов.", "error", 4000); }
            if (results[4].status === 'fulfilled' && results[4].value) { setAllTrainers(results[4].value); partialSuccess = true; }
            else { console.error("App.js: Failed to fetch trainers:", results[4].reason); setAllTrainers([]); showAppNotification("Ошибка загрузки списка тренеров.", "warning", 4000); }
            if (!partialSuccess && results.some(r => r.status === 'rejected')) {
                const firstError = results.find(r => r.status === 'rejected');
                setError(firstError?.reason?.message || "Не удалось загрузить основные данные приложения.");
            }
        } catch (overallError) {
            console.error("App.js: Critical error during Promise.allSettled in loadInitialData:", overallError);
            setError(overallError.message || "Критическая ошибка при загрузке данных.");
            setGroups([]); setHalls([]); setScheduleItems([]); setAllStudents([]); setAllTrainers([]);
        } finally {
            setIsLoading(false);
            // console.log("App.js: loadInitialData - FINISHED. isLoading: false");
        }
    }, [showAppNotification]);

    useEffect(() => {
        // console.log("App.js: useEffect for initial data load triggered");
        loadInitialData();
    }, [loadInitialData]);

    const handleOpenAddModal = useCallback((initialDataForModal = null) => {
        setModalInitialData(initialDataForModal); setModalExistingItem(null); setIsModalOpen(true);
    }, []);
    const handleOpenViewModal = useCallback((item) => {
        setModalExistingItem(item); setModalInitialData(null); setIsModalOpen(true);
    }, []);
    const handleCloseModal = useCallback(() => {
        setIsModalOpen(false); setModalInitialData(null); setModalExistingItem(null);
    }, []);

    const handleSaveSchedule = useCallback(async (newScheduleData) => {
        try {
            await addScheduleItem(newScheduleData);
            showAppNotification("Занятие успешно добавлено!", "success");
            handleCloseModal();
            await reloadScheduleItems();
        } catch (error) {
            let detailedErrorMessage;
            if (error.status === 409) { detailedErrorMessage = "Данное время в этом зале занято"; }
            else { const apiMsg = error.message || "Неизв.ошибка"; detailedErrorMessage = `Ошибка добавления: ${apiMsg.replace(/^Не удалось.+Ошибка API: \d{3}\. /,'').replace(/^Ошибка API: \d{3}\. /,'') || "сервера."}`; }
            showAppNotification(detailedErrorMessage, "error", 6000);
        }
    }, [reloadScheduleItems, showAppNotification, handleCloseModal]);

    const handleUpdateScheduleItem = useCallback(async (itemId, updatedScheduleData) => {
        try {
            await updateScheduleItem(itemId, updatedScheduleData);
            showAppNotification("Занятие успешно обновлено!", "success");
            handleCloseModal();
            await reloadScheduleItems();
        } catch (error) {
            let detailedErrorMessage;
            if (error.status === 409) { detailedErrorMessage = "Данное время в этом зале занято"; }
            else { const apiMsg = error.message || "Неизв.ошибка"; detailedErrorMessage = `Ошибка обновления: ${apiMsg.replace(/^Не удалось.+Ошибка API: \d{3}\. /,'').replace(/^Ошибка API: \d{3}\. /,'') || "сервера."}`; }
            showAppNotification(detailedErrorMessage, "error", 7000);
        }
    }, [reloadScheduleItems, showAppNotification, handleCloseModal]);

    const handleDeleteScheduleItem = useCallback(async (itemId) => {
        try {
            await deleteScheduleItem(itemId);
            showAppNotification("Занятие успешно удалено!", "success");
            handleCloseModal();
            await reloadScheduleItems();
        } catch (error) {
            const apiMsg = error.message || "Неизв.ошибка";
            showAppNotification(`Ошибка удаления: ${apiMsg.replace(/^Не удалось.+Ошибка API: \d{3}\. /,'').replace(/^Ошибка API: \d{3}\. /,'') || "сервера."}`, "error");
        }
    }, [reloadScheduleItems, showAppNotification, handleCloseModal]);

    const appContextValue = {
        halls, groups, scheduleItems, allStudents, allTrainers,
        isLoading, // Передаем isLoading как isLoadingAppLevelData через контекст
        error,
        handleOpenAddModal, handleOpenViewModal,
        showAppNotification, loadInitialData
    };

    return (
        <AppContext.Provider value={appContextValue}>
            <Router>
                <div className="app-layout"> {/* Обертка для управления высотой */}
                    <Navbar />
                    {notification.show && ( <div className={`app-notification ${notification.type} ${notification.show ? 'show' : ''}`}>{notification.message}</div> )}

                    <div className="container mt-4 page-content-area"> {/* Для основного контента страницы */}
                        <AnimatedRoutes /> {/* Анимированные маршруты */}
                    </div>

                    <AddScheduleModal
                        isOpen={isModalOpen}
                        onClose={handleCloseModal}
                        onSave={handleSaveSchedule}
                        onUpdate={handleUpdateScheduleItem}
                        onDelete={handleDeleteScheduleItem}
                        groups={groups}
                        halls={halls}
                        initialData={modalInitialData}
                        existingItem={modalExistingItem}
                    />
                </div>
            </Router>
        </AppContext.Provider>
    );
}

export default App;