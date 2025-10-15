import { Modal, Button, Form } from 'react-bootstrap';

const ScheduleModal = ({ show, onClose, item }) => {
    return (
        <Modal show={show} onHide={onClose}>
            <Modal.Header closeButton>
                <Modal.Title>{item ? 'Редактирование' : 'Новое занятие'}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group className="mb-3">
                        <Form.Label>Группа</Form.Label>
                        <Form.Control
                            type="text"
                            defaultValue={item?.group?.name || ''}
                        />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Тренер</Form.Label>
                        <Form.Control
                            type="text"
                            defaultValue={item?.trainer?.name || ''}
                        />
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onClose}>Закрыть</Button>
                <Button variant="primary">Сохранить</Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ScheduleModal;