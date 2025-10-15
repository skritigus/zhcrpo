package com.bootgussy.dancecenterservice.core.config;

import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.Hall;
import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import com.bootgussy.dancecenterservice.core.model.Student;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CacheConfig {
    private static final int MAX_GROUP_CACHE_SIZE = 100;
    private static final int MAX_HALL_CACHE_SIZE = 100;
    private static final int MAX_SCHEDULE_ITEM_CACHE_SIZE = 100;
    private static final int MAX_STUDENT_CACHE_SIZE = 100;
    private static final int MAX_TRAINER_CACHE_SIZE = 100;
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfig.class);

    private final LruCache<Long, Group> groupCache =
            new LruCache<>(MAX_GROUP_CACHE_SIZE);
    private final LruCache<Long, Hall> hallCache =
            new LruCache<>(MAX_HALL_CACHE_SIZE);
    private final LruCache<Long, ScheduleItem> scheduleItemCache =
            new LruCache<>(MAX_SCHEDULE_ITEM_CACHE_SIZE);
    private final LruCache<Long, Student> studentCache =
            new LruCache<>(MAX_STUDENT_CACHE_SIZE);
    private final LruCache<Long, Trainer> trainerCache =
            new LruCache<>(MAX_TRAINER_CACHE_SIZE);

    public Group getGroup(Long id) {
        LOGGER.info("Get group cache for {}", id);
        return groupCache.get(id);
    }

    public void putGroup(Long id, Group group) {
        groupCache.put(id, group);
    }

    public void removeGroup(Long id) {
        groupCache.remove(id);
    }

    public Hall getHall(Long id) {
        LOGGER.info("Get hall cache for {}", id);
        return hallCache.get(id);
    }

    public void putHall(Long id, Hall hall) {
        hallCache.put(id, hall);
    }

    public void removeHall(Long id) {
        hallCache.remove(id);
    }

    public ScheduleItem getScheduleItem(Long id) {
        LOGGER.info("Get schedule item cache for {}", id);
        return scheduleItemCache.get(id);
    }

    public void putScheduleItem(Long id, ScheduleItem scheduleItem) {
        scheduleItemCache.put(id, scheduleItem);
    }

    public void removeScheduleItem(Long id) {
        scheduleItemCache.remove(id);
    }

    public Student getStudent(Long id) {
        LOGGER.info("Get student cache for {}", id);
        return studentCache.get(id);
    }

    public void putStudent(Long id, Student student) {
        studentCache.put(id, student);
    }

    public void removeStudent(Long id) {
        studentCache.remove(id);
    }

    public Trainer getTrainer(Long id) {
        LOGGER.info("Get trainer cache for {}", id);
        return trainerCache.get(id);
    }

    public void putTrainer(Long id, Trainer trainer) {
        trainerCache.put(id, trainer);
    }

    public void removeTrainer(Long id) {
        trainerCache.remove(id);
    }
}
