import { useState } from "react";
import { createStudent } from "../../services/api";

const phoneRegex = /^\+?[0-9]{10,15}$/;
const nameRegex = /^[A-Za-zА-Яа-яЁё\s-]{2,30}$/;

const RegisterForm = ({ onSuccess }) => {
    const [formData, setFormData] = useState({
        name: "",
        phoneNumber: "",
        password: ""
    });

    const [loading, setLoading] = useState(false);
    const [serverError, setServerError] = useState("");
    const [errors, setErrors] = useState({});

    const validate = () => {
        const newErrors = {};

        if (!nameRegex.test(formData.name)) {
            newErrors.name = "Имя может содержать только буквы";
        }

        if (!phoneRegex.test(formData.phoneNumber)) {
            newErrors.phoneNumber = "Введите корректный номер телефона";
        }

        if (formData.password.length < 6) {
            newErrors.password = "Пароль должен содержать минимум 6 символов";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validate()) return;

        setLoading(true);
        setServerError("");

        try {
            // вызываем новый API для регистрации
            const newUser = await register({
                name: formData.name,
                phoneNumber: formData.phoneNumber,
                password: formData.password // если сервер ожидает пароль
            });

            // передаем созданного пользователя наверх, например для закрытия модалки
            if (onSuccess) onSuccess(newUser);

        } catch (error) {
            // показываем сообщение об ошибке от API
            setServerError(error.message || "Ошибка при регистрации");
        } finally {
            setLoading(false);
        }
    };

    return (
        <form className="auth-form" onSubmit={handleSubmit}>
            <div className="input-group">
                <input
                    type="text"
                    name="name"
                    placeholder="Имя"
                    value={formData.name}
                    onChange={handleChange}
                />
                {errors.name && <span className="error">{errors.name}</span>}
            </div>

            <div className="input-group">
                <input
                    type="tel"
                    name="phoneNumber"
                    placeholder="Номер телефона"
                    value={formData.phoneNumber}
                    onChange={handleChange}
                />
                {errors.phoneNumber && <span className="error">{errors.phoneNumber}</span>}
            </div>

            <div className="input-group">
                <input
                    type="password"
                    name="password"
                    placeholder="Пароль"
                    value={formData.password}
                    onChange={handleChange}
                />
                {errors.password && <span className="error">{errors.password}</span>}
            </div>

            <button type="submit" className="auth-submit" disabled={loading}>
                {loading ? "Загрузка..." : "Зарегистрироваться"}
            </button>
            {serverError && <div className="server-error">{serverError}</div>}
        </form>
    );
};

export default RegisterForm;
