package com.bootgussy.dancecenterservice.core.service;

import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import java.util.List;

public interface ScheduleItemService {
    ScheduleItem findScheduleItemById(Long id);

    List<ScheduleItem> findAllScheduleItems();

    List<ScheduleItem> findAllScheduleItemsByGroup(Long groupId);

    ScheduleItem createScheduleItem(ScheduleItem scheduleItem);

    List<ScheduleItem> createMultipleScheduleItems(List<ScheduleItem> scheduleItems);

    ScheduleItem updateScheduleItem(ScheduleItem scheduleItem);

    void deleteScheduleItem(Long id);
}
