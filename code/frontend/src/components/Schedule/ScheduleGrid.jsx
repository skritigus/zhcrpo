// src/components/Schedule/ScheduleGrid.jsx
import React from 'react';
import './../../styles/ScheduleGrid.css';
import ScheduleCellContent from './ScheduleCellContent';

export default function ScheduleGrid({ hall, groups, scheduleItems, onSlotClick, onItemClick }) {
    const displayDaysConfig = [
        { key: "Monday", label: "ПН" }, { key: "Tuesday", label: "ВТ" },
        { key: "Wednesday", label: "СР" }, { key: "Thursday", label: "ЧТ" },
        { key: "Friday", label: "ПТ" }, { key: "Saturday", label: "СБ" },
        { key: "Sunday", label: "ВС" },
    ];

    const logicalTimeSlots = [];
    for (let hour = 8; hour <= 22; hour++) {
        logicalTimeSlots.push(`${hour < 10 ? '0' : ''}${hour}:00`);
    }
    const displayTimeLabels = logicalTimeSlots.map(slot => `${slot} - ${String(parseInt(slot.substring(0,2)) + 1).padStart(2, '0')}:00`);


    const isTimeSlotOccupied = (logicalTimeCell, itemStartTime, itemEndTime) => {
        const cellStart = logicalTimeCell;
        const cellEndHour = parseInt(logicalTimeCell.substring(0, 2)) + 1;
        const cellEnd = `${cellEndHour < 10 ? '0' : ''}${cellEndHour}:00`;
        const itemStart = itemStartTime.substring(0, 5);
        const itemEnd = itemEndTime.substring(0, 5);
        return itemStart < cellEnd && itemEnd > cellStart;
    };

    return (
        <div className="schedule-container">
            <table className="schedule-table">
                <thead>
                <tr>
                    <th className="time-header">Время</th>
                    {displayDaysConfig.map(dayConfig => ( <th key={dayConfig.key}>{dayConfig.label}</th> ))}
                </tr>
                </thead>
                <tbody>
                {logicalTimeSlots.map((logicalTimeCell, index) => (
                    <tr key={logicalTimeCell}>
                        <td className="time-cell">{displayTimeLabels[index]}</td>
                        {displayDaysConfig.map(dayConfig => {
                            const item = scheduleItems.find(scheduleItem =>
                                scheduleItem.dayOfWeek === dayConfig.key &&
                                isTimeSlotOccupied(logicalTimeCell, scheduleItem.startTime, scheduleItem.endTime)
                            );
                            const dayCellClassName = `day-cell ${item ? 'occupied' : 'empty'}`;
                            return (
                                <td
                                    key={`${dayConfig.key}-${logicalTimeCell}`}
                                    className={dayCellClassName}
                                    onClick={() => {
                                        if (item) {
                                            console.log("ScheduleGrid: Click on OCCUPIED cell, item:", item);
                                            if (onItemClick) onItemClick(item); else console.warn("ScheduleGrid: onItemClick is undefined!");
                                        } else {
                                            console.log("ScheduleGrid: Click on EMPTY cell, day:", dayConfig.key, "time:", logicalTimeCell);
                                            if (onSlotClick) onSlotClick(dayConfig.key, logicalTimeCell); else console.warn("ScheduleGrid: onSlotClick is undefined!");
                                        }
                                    }}
                                >
                                    {item && <ScheduleCellContent item={item} />}

                                </td>
                            );
                        })}
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}