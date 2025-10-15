package com.bootgussy.dancecenterservice.service;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.Student;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import com.bootgussy.dancecenterservice.core.repository.GroupRepository;
import com.bootgussy.dancecenterservice.core.repository.StudentRepository;
import com.bootgussy.dancecenterservice.core.repository.TrainerRepository;
import com.bootgussy.dancecenterservice.core.service.impl.GroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupServiceImplTest {

    @InjectMocks
    private GroupServiceImpl groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CacheConfig cacheConfig;

    private Group group;
    private Group group2;
    private Trainer trainer;
    private Student student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trainer = new Trainer(1L, "Trainer Name",  null, null, null);
        student = new Student(1L, "Student Name", null, null);
        group = new Group(1L, "Beginner", trainer, Collections.singletonList(student), null);
        group2 = new Group(2L, "Intermediate", trainer, Collections.singletonList(student), null);
    }

    @Test
    void findGroupById_GroupExists_ReturnsGroup() {
        when(cacheConfig.getGroup(1L)).thenReturn(null);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        Group foundGroup = groupService.findGroupById(1L);

        assertNotNull(foundGroup);
        assertEquals(group.getId(), foundGroup.getId());
        verify(cacheConfig).putGroup(1L, group);
    }

    @Test
    void findGroupById_GroupNotFound_ThrowsException() {
        when(cacheConfig.getGroup(1L)).thenReturn(null);
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            groupService.findGroupById(1L);
        });

        assertEquals("Group not found. ID: 1", exception.getMessage());
    }

    @Test
    void findGroupById_GroupInCache_ReturnsGroupFromCache() {
        when(cacheConfig.getGroup(1L)).thenReturn(group);

        Group foundGroup = groupService.findGroupById(1L);

        assertNotNull(foundGroup);
        assertEquals(group.getId(), foundGroup.getId());
        verify(groupRepository, never()).findById(1L);
    }

    @Test
    void findAllGroups_ReturnsAllGroups() {
        List<Group> groups = Arrays.asList(group, group2);
        when(groupRepository.findAll()).thenReturn(groups);

        List<Group> foundGroups = groupService.findAllGroups();

        assertNotNull(foundGroups);
        assertEquals(2, foundGroups.size());
        assertEquals(groups, foundGroups);
    }

    @Test
    void findAllGroupsByDanceStyle_ReturnsFilteredGroups() {
        String danceStyle = "Ballet";
        List<Group> groups = Arrays.asList(group);
        when(groupRepository.findAllByDanceStyle(danceStyle)).thenReturn(groups);
        List<Group> foundGroups = groupService.findAllGroupsByDanceStyle(danceStyle);

        assertNotNull(foundGroups);
        assertEquals(1, foundGroups.size());
        assertEquals(groups, foundGroups);
    }

    @Test
    void findAllGroupsByDanceStyle_NoGroupsFound_ReturnsEmptyList() {
        String danceStyle = "Hip Hop";
        when(groupRepository.findAllByDanceStyle(danceStyle)).thenReturn(Arrays.asList());

        List<Group> foundGroups = groupService.findAllGroupsByDanceStyle(danceStyle);

        assertNotNull(foundGroups);
        assertTrue(foundGroups.isEmpty());
    }

    @Test
    void createGroup_ValidGroup_CreatesGroup() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(studentRepository.existsById(student.getId())).thenReturn(true);
        when(groupRepository.findByTrainerAndDifficulty(trainer, "Beginner")).thenReturn(Optional.empty()); // Возвращаем пустой список
        when(groupRepository.save(group)).thenReturn(group);

        Group createdGroup = groupService.createGroup(group);

        assertNotNull(createdGroup);
        assertEquals(group.getId(), createdGroup.getId());
        verify(cacheConfig).putGroup(group.getId(), createdGroup);
    }

    @Test
    void createGroup_TrainerNotFound_ThrowsException() {
        group.setTrainer(new Trainer(2L, "Trainer Name", null, null, null));
        group.setStudents(new ArrayList<>());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            groupService.createGroup(group);
        });

        assertEquals("Trainer not found. ID: 2", exception.getMessage());
    }

    @Test
    void createGroup_StudentNotFound_ThrowsException() {
        group.setStudents(Collections.singletonList(new Student(2L, "Invalid Student", null, null)));

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(studentRepository.existsById(2L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            groupService.createGroup(group);
        });

        assertEquals("Incorrect Student ID. Student ID: 2", exception.getMessage());
    }

    @Test
    void createGroup_MissingFields_ThrowsException() {
        group.setDifficulty(null); // Удаляем уровень сложности

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            groupService.createGroup(group);
        });

        assertEquals("Incorrect JSON. All fields must be filled (trainerId, difficulty).", exception.getMessage());
    }

    @Test
    void createGroup_GroupAlreadyExists_ThrowsException() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(studentRepository.existsById(student.getId())).thenReturn(true);
        when(groupRepository.findByTrainerAndDifficulty(trainer, "Beginner")).thenReturn(Optional.of(group));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            groupService.createGroup(group);
        });

        assertEquals("Group already exists. Trainer: Trainer Name, Difficulty: Beginner", exception.getMessage());
    }

    @Test
    void updateGroup_ValidGroup_UpdatesGroup() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(studentRepository.existsById(student.getId())).thenReturn(true);
        when(groupRepository.findByTrainerAndDifficulty(trainer, "Beginner")).thenReturn(Optional.empty());
        when(groupRepository.save(group)).thenReturn(group);

        Group updatedGroup = groupService.updateGroup(group);

        assertNotNull(updatedGroup);
        assertEquals(group.getId(), updatedGroup.getId());
        verify(cacheConfig).putGroup(group.getId(), updatedGroup);
    }

    @Test
    void updateGroup_GroupNotFound_ThrowsException() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.empty());
        group.setStudents(new ArrayList<>());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            groupService.updateGroup(group);
        });

        assertEquals("The group does not exist. ID: 1, Trainer: Trainer Name, Difficulty: Beginner", exception.getMessage());
    }

    @Test
    void updateGroup_InvalidTrainer_ThrowsException() {
        group.setTrainer(null); // Устанавливаем тренера в null

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            groupService.updateGroup(group);
        });

        assertEquals("Incorrect JSON. All fields must be filled (trainerId, difficulty).", exception.getMessage());
    }

    @Test
    void updateGroup_InvalidStudent_ThrowsException() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(studentRepository.existsById(student.getId())).thenReturn(false); // Студент не найден

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            groupService.updateGroup(group);
        });

        assertEquals("Incorrect Student ID. Student ID: 1", exception.getMessage());
    }

    @Test
    void updateGroup_GroupAlreadyExists_ThrowsException() {
        Group existingGroup = new Group(2L, "Beginner", trainer, Collections.singletonList(student), null);
        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(studentRepository.existsById(student.getId())).thenReturn(true);
        when(groupRepository.findByTrainerAndDifficulty(trainer, "Beginner")).thenReturn(Optional.of(existingGroup));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            groupService.updateGroup(group);
        });

        assertEquals("Group already exists. TrainerId: 1, Difficulty: Beginner", exception.getMessage());
    }

    @Test
    void deleteGroup_ValidId_DeletesGroup() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        groupService.deleteGroup(1L);

        verify(groupRepository).delete(group);
        verify(cacheConfig).removeGroup(1L);
    }

    @Test
    void deleteGroup_GroupNotFound_ThrowsException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            groupService.deleteGroup(1L);
        });

        assertEquals("Group not found. ID: 1", exception.getMessage());
    }
}