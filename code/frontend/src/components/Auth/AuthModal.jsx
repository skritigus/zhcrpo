import { useState } from "react";
import LoginForm from "./LoginForm";
import RegisterForm from "./RegisterForm";
import "../../styles/AuthModal.css";

const AuthModal = ({ isOpen, onClose }) => {
    const [isLogin, setIsLogin] = useState(true);

    if (!isOpen) return null;

    return (
        <div className="auth-modal-overlay" onClick={onClose}>
            <div className="auth-modal" onClick={e => e.stopPropagation()}>
                <div className="auth-tabs">
                    <button
                        className={isLogin ? "active" : ""}
                        onClick={() => setIsLogin(true)}
                    >
                        Вход
                    </button>
                    <button
                        className={!isLogin ? "active" : ""}
                        onClick={() => setIsLogin(false)}
                    >
                        Регистрация
                    </button>
                </div>
                <div className="auth-form">
                    {isLogin ? <LoginForm onSuccess={onClose} /> : <RegisterForm onSuccess={onClose} />}
                </div>

                <button className="auth-close-btn" onClick={onClose}>
                    ✕
                </button>
            </div>
        </div>
    );
};

export default AuthModal;
