package com.bootgussy.dancecenterservice.service;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.IncorrectDataException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.Hall;
import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import com.bootgussy.dancecenterservice.core.repository.ScheduleItemRepository;
import com.bootgussy.dancecenterservice.core.service.impl.ScheduleItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScheduleItemServiceImplTest {
    @InjectMocks
    private ScheduleItemServiceImpl scheduleItemService;

    @Mock
    private ScheduleItemRepository scheduleItemRepository;

    @Mock
    private CacheConfig cacheConfig;

    private ScheduleItem scheduleItem;

    private Hall hall;

    private Group group;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hall = new Hall(1L, "Main Hall", 200, null);
        group = new Group(1L, "Beg", null, null, null);
        scheduleItem = new ScheduleItem(1L, hall, group, "Monday",
                LocalTime.of(10, 0), LocalTime.of(11, 0));
    }

    @Test
    void findScheduleItemById_ExistingItem_ReturnsItem() {
        when(cacheConfig.getScheduleItem(scheduleItem.getId())).thenReturn(null);
        when(scheduleItemRepository.findById(scheduleItem.getId())).thenReturn(Optional.of(scheduleItem));

        ScheduleItem foundItem = scheduleItemService.findScheduleItemById(scheduleItem.getId());

        assertNotNull(foundItem);
        assertEquals(scheduleItem.getId(), foundItem.getId());
        verify(cacheConfig).putScheduleItem(scheduleItem.getId(), scheduleItem);
    }

    @Test
    void findScheduleItemById_NonExistingItem_ThrowsException() {
        when(cacheConfig.getScheduleItem(scheduleItem.getId())).thenReturn(null);
        when(scheduleItemRepository.findById(scheduleItem.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> scheduleItemService.findScheduleItemById(scheduleItem.getId())
        );

        assertEquals("Schedule item not found. ID: 1", exception.getMessage());
    }

    @Test
    void findAllScheduleItems_ReturnsAllItems() {
        when(scheduleItemRepository.findAll()).thenReturn(Collections.singletonList(scheduleItem));

        List<ScheduleItem> items = scheduleItemService.findAllScheduleItems();

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(scheduleItem, items.get(0));
    }

    @Test
    void createScheduleItem_ValidItem_CreatesItem() {
        when(scheduleItemRepository.findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
                scheduleItem.getHall(), scheduleItem.getGroup(), scheduleItem.getDayOfWeek(),
                scheduleItem.getStartTime(), scheduleItem.getEndTime())).thenReturn(Collections.emptyList());
        when(scheduleItemRepository.save(scheduleItem)).thenReturn(scheduleItem);

        ScheduleItem createdItem = scheduleItemService.createScheduleItem(scheduleItem);

        assertNotNull(createdItem);
        assertEquals(scheduleItem.getId(), createdItem.getId());
        verify(cacheConfig).putScheduleItem(scheduleItem.getId(), createdItem);
    }

    @Test
    void createScheduleItem_InvalidDayOfWeek_ThrowsException() {
        scheduleItem.setDayOfWeek("Invalid Day");

        IncorrectDataException exception = assertThrows(IncorrectDataException.class, () -> {
            scheduleItemService.createScheduleItem(scheduleItem);
        });

        assertEquals("Day of week is incorrect. Example: Monday", exception.getMessage());
    }

    @Test
    void createScheduleItem_ItemAlreadyExists_ThrowsException() {
        when(scheduleItemRepository.findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
                scheduleItem.getHall(), scheduleItem.getGroup(), scheduleItem.getDayOfWeek(),
                scheduleItem.getStartTime(), scheduleItem.getEndTime())).thenReturn(List.of(scheduleItem));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            scheduleItemService.createScheduleItem(scheduleItem);
        });

        assertEquals("Schedule item already exists. Group: 1, Hall: 1, Day of week: Monday, Start time: 10:00, End time: 11:00", exception.getMessage());
    }

    @Test
    void createScheduleItem_NullHall_ThrowsException() {
        scheduleItem.setHall(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.createScheduleItem(scheduleItem);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void createScheduleItem_NullGroup_ThrowsException() {
        scheduleItem.setGroup(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.createScheduleItem(scheduleItem);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void createScheduleItem_NullDayOfWeek_ThrowsException() {
        scheduleItem.setDayOfWeek(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.createScheduleItem(scheduleItem);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void createScheduleItem_NullStartTime_ThrowsException() {
        scheduleItem.setStartTime(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.createScheduleItem(scheduleItem);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void createScheduleItem_OverlappingTime_ThrowsException() {
        ScheduleItem conflictingItem = new ScheduleItem(2L, hall, group, "Monday",
                LocalTime.of(10, 30), LocalTime.of(11, 30));
        when(scheduleItemRepository.findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
                scheduleItem.getHall(), scheduleItem.getGroup(), scheduleItem.getDayOfWeek(),
                scheduleItem.getStartTime(), scheduleItem.getEndTime())).thenReturn(Collections.emptyList());
        when(scheduleItemRepository.findByDayOfWeekAndHall(scheduleItem.getDayOfWeek(), scheduleItem.getHall()))
                .thenReturn(List.of(conflictingItem)); // Существующий элемент с наложением

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            scheduleItemService.createScheduleItem(scheduleItem);
        });

        assertEquals("This time in this hall is busy. Group ID: 1, Start time: 10:00, End time: 11:00", exception.getMessage());
    }

    @Test
    void createScheduleItem_DuplicateTimeInBusyHall_ThrowsException() {
        ScheduleItem busyItem = new ScheduleItem(2L, hall, group, "Monday",
                LocalTime.of(10, 0), LocalTime.of(11, 0));
        when(scheduleItemRepository.findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
                hall, group, "Monday", LocalTime.of(10, 0), LocalTime.of(11, 0)))
                .thenReturn(List.of(busyItem)); // Зал уже занят в это время

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            scheduleItemService.createScheduleItem(scheduleItem);
        });

        assertEquals("Schedule item already exists. Group: 1, Hall: 1, Day of week: Monday, Start time: 10:00, End time: 11:00", exception.getMessage());
    }

    @Test
    void createScheduleItem_InvalidHall_ThrowsException() {
        scheduleItem.setHall(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.createScheduleItem(scheduleItem);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void createScheduleItem_ValidData_Success() {
        when(scheduleItemRepository.save(any(ScheduleItem.class))).thenReturn(scheduleItem);

        ScheduleItem createdItem = scheduleItemService.createScheduleItem(scheduleItem);

        assertNotNull(createdItem);
        assertEquals(scheduleItem.getId(), createdItem.getId());
    }

    @Test
    void createScheduleItem_ValidTimeRange_Success() {
        when(scheduleItemRepository.findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
                scheduleItem.getHall(), scheduleItem.getGroup(), scheduleItem.getDayOfWeek(),
                scheduleItem.getStartTime(), scheduleItem.getEndTime())).thenReturn(Collections.emptyList());
        when(scheduleItemRepository.save(scheduleItem)).thenReturn(scheduleItem);

        ScheduleItem createdItem = scheduleItemService.createScheduleItem(scheduleItem);

        assertNotNull(createdItem);
        assertEquals(scheduleItem.getId(), createdItem.getId());
    }

    @Test
    void createMultipleScheduleItems_ValidItems_CreatesItems() {
        List<ScheduleItem> items = List.of(scheduleItem);
        when(scheduleItemRepository.saveAll(items)).thenReturn(items);

        List<ScheduleItem> createdItems = scheduleItemService.createMultipleScheduleItems(items);

        assertNotNull(createdItems);
        assertEquals(1, createdItems.size());
        assertEquals(scheduleItem, createdItems.get(0));
        verify(cacheConfig).putScheduleItem(scheduleItem.getId(), createdItems.get(0));
    }

    @Test
    void createMultipleScheduleItems_InvalidDayOfWeek_ThrowsException() {
        ScheduleItem invalidItem = new ScheduleItem(2L, hall, group, "Invalid Day",
                LocalTime.of(10, 0), LocalTime.of(11, 0));
        List<ScheduleItem> items = List.of(scheduleItem, invalidItem);

        IncorrectDataException exception = assertThrows(IncorrectDataException.class, () -> {
            scheduleItemService.createMultipleScheduleItems(items);
        });

        assertEquals("Day of week is incorrect. Example: Monday", exception.getMessage());
    }

    @Test
    void createMultipleScheduleItems_NullEndTime_ThrowsException() {
        ScheduleItem invalidItem = new ScheduleItem(2L, hall, group, "Monday",
                LocalTime.of(10, 0), null);
        List<ScheduleItem> items = List.of(scheduleItem, invalidItem);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.createMultipleScheduleItems(items);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void createMultipleScheduleItems_ValidItemsWithOverlap_ThrowsException() {
        ScheduleItem overlappingItem = new ScheduleItem(2L, hall, group, "Monday",
                LocalTime.of(10, 30), LocalTime.of(11, 30));
        List<ScheduleItem> items = List.of(scheduleItem, overlappingItem);

        when(scheduleItemRepository.findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
                hall, group, "Monday", LocalTime.of(10, 30), LocalTime.of(11, 30)))
                .thenReturn(List.of(overlappingItem)); // Перекрытие времени

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            scheduleItemService.createMultipleScheduleItems(items);
        });

        assertEquals("Schedule item already exists. Group: 1, Hall: 1, Day of week: Monday, Start time: 10:30, End time: 11:30", exception.getMessage());
    }

    @Test
    void updateScheduleItem_ValidItem_UpdatesItem() {
        when(scheduleItemRepository.findById(scheduleItem.getId())).thenReturn(Optional.of(scheduleItem));
        when(scheduleItemRepository.save(scheduleItem)).thenReturn(scheduleItem);

        ScheduleItem updatedItem = scheduleItemService.updateScheduleItem(scheduleItem);

        assertNotNull(updatedItem);
        assertEquals(scheduleItem.getId(), updatedItem.getId());
        verify(cacheConfig).putScheduleItem(scheduleItem.getId(), updatedItem);
    }

    @Test
    void updateScheduleItem_ItemNotFound_ThrowsException() {
        when(scheduleItemRepository.findById(scheduleItem.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.updateScheduleItem(scheduleItem);
        });

        assertEquals("The schedule item does not exist. ID: 1, Group: 1, Hall: 1, Day of week: Monday, Start time: 10:00, End time: 11:00", exception.getMessage());
    }

    @Test
    void updateScheduleItem_InvalidDayOfWeek_ThrowsException() {
        scheduleItem.setDayOfWeek("Invalid Day");
        when(scheduleItemRepository.findById(scheduleItem.getId())).thenReturn(Optional.of(scheduleItem));

        IncorrectDataException exception = assertThrows(IncorrectDataException.class, () -> {
            scheduleItemService.updateScheduleItem(scheduleItem);
        });

        assertEquals("Day of week is incorrect. Example: Monday", exception.getMessage());
    }

    @Test
    void updateScheduleItem_TimeBusy_ThrowsException() {
        ScheduleItem existingItem = new ScheduleItem(2L, hall, group, "Monday",
                LocalTime.of(10, 30), LocalTime.of(11, 30));
        when(scheduleItemRepository.findById(scheduleItem.getId())).thenReturn(Optional.of(scheduleItem));
        when(scheduleItemRepository.findByDayOfWeekAndHall(scheduleItem.getDayOfWeek(), scheduleItem.getHall()))
                .thenReturn(List.of(existingItem));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            scheduleItemService.updateScheduleItem(scheduleItem);
        });

        assertEquals("This time in this hall is busy. Group ID: 1, Start time: 10:00, End time: 11:00", exception.getMessage());
    }

    @Test
    void updateScheduleItem_NullEndTime_ThrowsException() {
        scheduleItem.setEndTime(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.updateScheduleItem(scheduleItem);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void updateScheduleItem_NullGroup_ThrowsException() {
        scheduleItem.setGroup(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.updateScheduleItem(scheduleItem);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void updateScheduleItem_NullDayOfWeek_ThrowsException() {
        scheduleItem.setDayOfWeek(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.updateScheduleItem(scheduleItem);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void deleteScheduleItem_ValidId_DeletesItem() {
        when(scheduleItemRepository.findById(scheduleItem.getId())).thenReturn(Optional.of(scheduleItem));

        scheduleItemService.deleteScheduleItem(scheduleItem.getId());

        verify(scheduleItemRepository).delete(scheduleItem);
        verify(cacheConfig).removeScheduleItem(scheduleItem.getId());
    }

    @Test
    void deleteScheduleItem_NonExistingId_ThrowsException() {
        when(scheduleItemRepository.findById(scheduleItem.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> scheduleItemService.deleteScheduleItem(scheduleItem.getId())
        );

        assertEquals("Schedule item not found. ID: 1", exception.getMessage());
    }

    @Test
    void isValidDayOfWeek_ValidDays_ReturnsTrue() {
        assertTrue(scheduleItemService.isValidDayOfWeek("Monday"));
        assertTrue(scheduleItemService.isValidDayOfWeek("Tuesday"));
        assertTrue(scheduleItemService.isValidDayOfWeek("Wednesday"));
        assertTrue(scheduleItemService.isValidDayOfWeek("Thursday"));
        assertTrue(scheduleItemService.isValidDayOfWeek("Friday"));
        assertTrue(scheduleItemService.isValidDayOfWeek("Saturday"));
        assertTrue(scheduleItemService.isValidDayOfWeek("Sunday"));
    }

    @Test
    void isValidDayOfWeek_InvalidDays_ReturnsFalse() {
        assertFalse(scheduleItemService.isValidDayOfWeek("Funday"));
        assertFalse(scheduleItemService.isValidDayOfWeek("Holiday"));
        assertFalse(scheduleItemService.isValidDayOfWeek(null));
        assertFalse(scheduleItemService.isValidDayOfWeek(""));
    }

    @Test
    void deleteScheduleItem_ValidId_CachesRemoval() {
        when(scheduleItemRepository.findById(scheduleItem.getId())).thenReturn(Optional.of(scheduleItem));

        scheduleItemService.deleteScheduleItem(scheduleItem.getId());

        verify(cacheConfig).removeScheduleItem(scheduleItem.getId());
    }

    @Test
    void createScheduleItem_SuccessWithValidData() {
        when(scheduleItemRepository.findByHallAndGroupAndDayOfWeekAndStartTimeAndEndTime(
                scheduleItem.getHall(), scheduleItem.getGroup(), scheduleItem.getDayOfWeek(),
                scheduleItem.getStartTime(), scheduleItem.getEndTime())).thenReturn(Collections.emptyList());
        when(scheduleItemRepository.save(scheduleItem)).thenReturn(scheduleItem);

        ScheduleItem createdItem = scheduleItemService.createScheduleItem(scheduleItem);

        assertNotNull(createdItem);
        assertEquals(scheduleItem.getId(), createdItem.getId());
    }

    @Test
    void updateScheduleItem_ValidData_Success() {
        when(scheduleItemRepository.findById(scheduleItem.getId())).thenReturn(Optional.of(scheduleItem));
        when(scheduleItemRepository.save(scheduleItem)).thenReturn(scheduleItem);

        ScheduleItem updatedItem = scheduleItemService.updateScheduleItem(scheduleItem);

        assertNotNull(updatedItem);
        assertEquals(scheduleItem.getId(), updatedItem.getId());
    }

    @Test
    void createMultipleScheduleItems_ValidData_Success() {
        List<ScheduleItem> items = List.of(scheduleItem);
        when(scheduleItemRepository.saveAll(items)).thenReturn(items);

        List<ScheduleItem> createdItems = scheduleItemService.createMultipleScheduleItems(items);

        assertNotNull(createdItems);
        assertEquals(1, createdItems.size());
        assertEquals(scheduleItem, createdItems.get(0));
    }

    @Test
    void createScheduleItem_NullEndTime_ThrowsException() {
        scheduleItem.setEndTime(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.createScheduleItem(scheduleItem);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void createMultipleScheduleItems_SomeNullFields_ThrowsException() {
        ScheduleItem invalidItem = new ScheduleItem(2L, hall, group, "Monday", LocalTime.of(10, 0), null);
        List<ScheduleItem> items = List.of(scheduleItem, invalidItem);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.createMultipleScheduleItems(items);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }

    @Test
    void updateScheduleItem_NullStartTime_ThrowsException() {
        scheduleItem.setStartTime(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            scheduleItemService.updateScheduleItem(scheduleItem);
        });

        assertEquals("Incorrect JSON. All fields must be filled (hallId, groupId, dayOfWeek, startTime, endTime).", exception.getMessage());
    }
}