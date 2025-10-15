package com.bootgussy.dancecenterservice.core.service.impl;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.IncorrectDataException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import com.bootgussy.dancecenterservice.core.repository.ScheduleItemRepository;
import com.bootgussy.dancecenterservice.core.service.ScheduleItemService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleItemServiceImpl implements ScheduleItemService {
    private final ScheduleItemRepository scheduleItemRepository;
    private final CacheConfig cacheConfig;

    @Autowired
    public ScheduleItemServiceImpl(ScheduleItemRepository scheduleItemRepository,
                                   CacheConfig cacheConfig) {
        this.scheduleItemRepository = scheduleItemRepository;
        this.cacheConfig = cacheConfig;
    }

    @Override
    public ScheduleItem findScheduleItemById(Long id) {
        ScheduleItem cachedScheduleItem = cacheConfig.getScheduleItem(id);
        if (cachedScheduleItem != null) {
            return cachedScheduleItem;
        }

        ScheduleItem scheduleItem = scheduleItemRepository.findById(id).orElse(null);

        if (scheduleItem != null) {
            cacheConfig.putScheduleItem(id, scheduleItem);

            return scheduleItem;
        } else {
            throw new ResourceNotFoundException("Schedule item not found. ID: " + id);
        }
    }

    @Override
    public List<ScheduleItem> findAllScheduleItems() {
        return scheduleItemRepository.findAll();
    }

    public boolean isValidDayOfWeek(String dayOfWeek) {
        if (dayOfWeek == null) {
            return false;
        }
        return dayOfWeek.equals("Monday") || dayOfWeek.equals("Tuesday") ||
                dayOfWeek.equals("Wednesday") || dayOfWeek.equals("Thursday") ||
                dayOfWeek.equals("Friday") || dayOfWeek.equals("Saturday") ||
                dayOfWeek.equals("Sunday");
    }

    @Override
    public List<ScheduleItem> findAllScheduleItemsByGroup(Long groupId) {
        return scheduleItemRepository.findAllByGroup(groupId);
    }

    @Override
    public ScheduleItem createScheduleItem(ScheduleItem scheduleItem) {
        if (
                scheduleItem.getHall() == null ||
                        scheduleItem.getGroup() == null ||
                        scheduleItem.getDayOfWeek() == null ||
                        scheduleItem.getStartTime() == null ||
                        scheduleItem.getEndTime() == null
        ) {
            throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled " +
                    "(hallId, groupId, dayOfWeek, startTime, endTime).");
        }

        if (!isValidDayOfWeek(scheduleItem.getDayOfWeek())) {
            throw new IncorrectDataException("Day of week is incorrect. Example: Monday");
        }

        if (!scheduleItemRepository.findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
                scheduleItem.getHall(),
                scheduleItem.getGroup(),
                scheduleItem.getDayOfWeek(),
                scheduleItem.getStartTime(),
                scheduleItem.getEndTime()
        ).isEmpty()) {
            throw new AlreadyExistsException("Schedule item already exists." +
                    " Group: " + scheduleItem.getGroup().getId() +
                    ", Hall: " + scheduleItem.getHall().getId() +
                    ", Day of week: " + scheduleItem.getDayOfWeek() +
                    ", Start time: " + scheduleItem.getStartTime() +
                    ", End time: " + scheduleItem.getEndTime());
        }

        List<ScheduleItem> existingScheduleItems = scheduleItemRepository
                .findByDayOfWeekAndHall(scheduleItem.getDayOfWeek(), scheduleItem.getHall());

        boolean isTimeBusy = existingScheduleItems.stream().anyMatch(currentScheduleItem ->
                (currentScheduleItem.getStartTime().isBefore(scheduleItem.getEndTime()) &&
                        currentScheduleItem.getEndTime().isAfter(scheduleItem.getStartTime()))
        );

        if (isTimeBusy) {
            throw new AlreadyExistsException("This time in this hall is busy." +
                    " Group ID: " + scheduleItem.getGroup().getId() +
                    ", Start time: " + scheduleItem.getStartTime() +
                    ", End time: " + scheduleItem.getEndTime());
        }

        ScheduleItem savedScheduleItem = scheduleItemRepository.save(scheduleItem);

        cacheConfig.putScheduleItem(savedScheduleItem.getId(), savedScheduleItem);

        return savedScheduleItem;
    }

    @Override
    public List<ScheduleItem> createMultipleScheduleItems(List<ScheduleItem> scheduleItems) {
        for (ScheduleItem scheduleItem : scheduleItems) {
            if (
                    scheduleItem.getHall() == null ||
                            scheduleItem.getGroup() == null ||
                            scheduleItem.getDayOfWeek() == null ||
                            scheduleItem.getStartTime() == null ||
                            scheduleItem.getEndTime() == null
            ) {
                throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled " +
                        "(hallId, groupId, dayOfWeek, startTime, endTime).");
            }

            if (!isValidDayOfWeek(scheduleItem.getDayOfWeek())) {
                throw new IncorrectDataException("Day of week is incorrect. Example: Monday");
            }

            if (!scheduleItemRepository.findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
                    scheduleItem.getHall(),
                    scheduleItem.getGroup(),
                    scheduleItem.getDayOfWeek(),
                    scheduleItem.getStartTime(),
                    scheduleItem.getEndTime()
            ).isEmpty()) {
                throw new AlreadyExistsException("Schedule item already exists." +
                        " Group: " + scheduleItem.getGroup().getId() +
                        ", Hall: " + scheduleItem.getHall().getId() +
                        ", Day of week: " + scheduleItem.getDayOfWeek() +
                        ", Start time: " + scheduleItem.getStartTime() +
                        ", End time: " + scheduleItem.getEndTime());
            }

            List<ScheduleItem> existingScheduleItems = scheduleItemRepository
                    .findByDayOfWeekAndHall(scheduleItem.getDayOfWeek(), scheduleItem.getHall());

            boolean isTimeBusy = existingScheduleItems.stream().anyMatch(currentScheduleItem ->
                    (currentScheduleItem.getStartTime().isBefore(scheduleItem.getEndTime()) &&
                            currentScheduleItem.getEndTime().isAfter(scheduleItem.getStartTime()))
            );

            if (isTimeBusy) {
                throw new AlreadyExistsException("This time in this hall is busy." +
                        " Group ID: " + scheduleItem.getGroup().getId() +
                        ", Start time: " + scheduleItem.getStartTime() +
                        ", End time: " + scheduleItem.getEndTime());
            }
        }

        List<ScheduleItem> savedScheduleItems = scheduleItemRepository.saveAll(scheduleItems);

        for (ScheduleItem savedScheduleItem : savedScheduleItems) {
            cacheConfig.putScheduleItem(savedScheduleItem.getId(), savedScheduleItem);
        }

        return savedScheduleItems;
    }

    @Override
    public ScheduleItem updateScheduleItem(ScheduleItem scheduleItem) {
        if (
                scheduleItem.getHall() == null ||
                        scheduleItem.getGroup() == null ||
                        scheduleItem.getDayOfWeek() == null ||
                        scheduleItem.getStartTime() == null ||
                        scheduleItem.getEndTime() == null
        ) {
            throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled " +
                    "(hallId, groupId, dayOfWeek, startTime, endTime).");
        }

        if (!isValidDayOfWeek(scheduleItem.getDayOfWeek())) {
            throw new IncorrectDataException("Day of week is incorrect. Example: Monday");
        }

        List<ScheduleItem> existingScheduleItems = scheduleItemRepository
                .findByDayOfWeekAndHall(scheduleItem.getDayOfWeek(), scheduleItem.getHall());

        boolean isTimeBusy = existingScheduleItems.stream().anyMatch(currentScheduleItem ->
                (currentScheduleItem.getStartTime().isBefore(scheduleItem.getEndTime()) &&
                        currentScheduleItem.getEndTime().isAfter(scheduleItem.getStartTime()))
        );

        if (!scheduleItemRepository.findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
                scheduleItem.getHall(),
                scheduleItem.getGroup(),
                scheduleItem.getDayOfWeek(),
                scheduleItem.getStartTime(),
                scheduleItem.getEndTime()
        ).isEmpty()) {
            throw new AlreadyExistsException("Schedule item already exists." +
                    " Group: " + scheduleItem.getGroup().getId() +
                    ", Hall: " + scheduleItem.getHall().getId() +
                    ", Day of week: " + scheduleItem.getDayOfWeek() +
                    ", Start time: " + scheduleItem.getStartTime() +
                    ", End time: " + scheduleItem.getEndTime());
        }

        if (isTimeBusy) {
            throw new AlreadyExistsException("This time in this hall is busy." +
                    " Group ID: " + scheduleItem.getGroup().getId() +
                    ", Start time: " + scheduleItem.getStartTime() +
                    ", End time: " + scheduleItem.getEndTime());
        }

        ScheduleItem updatedScheduleItem;

        if (scheduleItemRepository.findById(scheduleItem.getId()).isPresent()) {
            updatedScheduleItem = scheduleItemRepository.save(scheduleItem);
        } else {
            throw new ResourceNotFoundException("The schedule item does not exist." +
                    " ID: " + scheduleItem.getId() +
                    ", Group: " + scheduleItem.getGroup().getId() +
                    ", Hall: " + scheduleItem.getHall().getId() +
                    ", Day of week: " + scheduleItem.getDayOfWeek() +
                    ", Start time: " + scheduleItem.getStartTime() +
                    ", End time: " + scheduleItem.getEndTime());
        }

        cacheConfig.putScheduleItem(scheduleItem.getId(), updatedScheduleItem);

        return updatedScheduleItem;
    }

    @Override
    public void deleteScheduleItem(Long id) {
        ScheduleItem scheduleItem = scheduleItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule item not found. ID: " + id));

        cacheConfig.removeScheduleItem(scheduleItem.getId());

        scheduleItemRepository.delete(scheduleItem);
    }
}
