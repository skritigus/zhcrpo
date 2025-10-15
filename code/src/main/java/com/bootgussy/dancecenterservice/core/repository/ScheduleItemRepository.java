package com.bootgussy.dancecenterservice.core.repository;

import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.Hall;
import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {
    List<ScheduleItem> findByDayOfWeekAndHall(String dayOfWeek, Hall hall);

    List<ScheduleItem> findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
            Hall hall,
            Group group,
            String dayOfWeek,
            LocalTime start,
            LocalTime endTime
    );

    @Query("SELECT s FROM ScheduleItem s WHERE s.group.id = :groupId")
    List<ScheduleItem> findAllByGroup(@Param("groupId") Long groupId);
}