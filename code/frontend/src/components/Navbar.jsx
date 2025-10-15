import { Link, useLocation } from 'react-router-dom';
import './../styles/Navbar.css';

const Navbar = () => {
    const location = useLocation();

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="navbar-brand">
                    <span className="logo-icon">💃</span>
                    <span className="logo-text">Dance Center</span>
                </Link>

                <div className="navbar-links">
                    <Link
                        to="/schedule"
                        className={`nav-link ${location.pathname === '/schedule' ? 'active' : ''}`}
                    >
                        Расписание
                    </Link>
                    <Link
                        to="/groups"
                        className={`nav-link ${location.pathname === '/groups' ? 'active' : ''}`}
                    >
                        Группы
                    </Link>
                    <Link
                        to="/trainers"
                        className={`nav-link ${location.pathname === '/trainers' ? 'active' : ''}`}
                    >
                        Тренеры
                    </Link>
                    <Link
                        to="/students"
                        className={`nav-link ${location.pathname === '/students' ? 'active' : ''}`}
                    >
                        Студенты
                    </Link>
                    <Link
                        to="/halls"
                        className={`nav-link ${location.pathname === '/halls' ? 'active' : ''}`}
                    >
                        Залы
                    </Link>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;