package com.bootgussy.dancecenterservice.core.service.impl;

import com.bootgussy.dancecenterservice.core.config.CacheConfig;
import com.bootgussy.dancecenterservice.core.exception.AlreadyExistsException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.Student;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import com.bootgussy.dancecenterservice.core.repository.GroupRepository;
import com.bootgussy.dancecenterservice.core.repository.StudentRepository;
import com.bootgussy.dancecenterservice.core.repository.TrainerRepository;
import com.bootgussy.dancecenterservice.core.service.GroupService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final TrainerRepository trainerRepository;
    private final StudentRepository studentRepository;
    private final CacheConfig cacheConfig;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository,
                            TrainerRepository trainerRepository,
                            StudentRepository studentRepository,
                            CacheConfig cacheConfig) {
        this.groupRepository = groupRepository;
        this.trainerRepository = trainerRepository;
        this.studentRepository = studentRepository;
        this.cacheConfig = cacheConfig;
    }

    @Override
    public Group findGroupById(Long id) {
        Group cachedGroup = cacheConfig.getGroup(id);
        if (cachedGroup != null) {
            return cachedGroup;
        }

        Group group = groupRepository.findById(id).orElse(null);

        if (group != null) {
            cacheConfig.putGroup(id, group);

            return group;
        } else {
            throw new ResourceNotFoundException("Group not found. ID: " + id);
        }
    }

    @Override
    public List<Group> findAllGroups() {
        return groupRepository.findAll();
    }

    @Override
    public List<Group> findAllGroupsByDanceStyle(String danceStyle) {
        return groupRepository.findAllByDanceStyle(danceStyle);
    }

    @Override
    public Group createGroup(Group group) {
        if (group.getDifficulty() == null || group.getTrainer() == null) {
            throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled " +
                    "(trainerId, difficulty).");
        }

        if (group.getStudents() != null) {
            for (Student student : group.getStudents()) {
                if (!studentRepository.existsById(student.getId())) {
                    throw new ResourceNotFoundException("Incorrect Student ID. Student ID: " +
                            student.getId());
                }
            }
        }

        if (group.getTrainer().getId() != null) {
            Trainer managedTrainer = trainerRepository.findById(group.getTrainer().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found. ID: " +
                            group.getTrainer().getId()));
            group.setTrainer(managedTrainer);
        }

        if (groupRepository.findByTrainerAndDifficulty(group.getTrainer(), group.getDifficulty()).isEmpty()) {
            Group savedGroup = groupRepository.save(group);

            cacheConfig.putGroup(savedGroup.getId(), savedGroup);

            return savedGroup;
        } else {
            throw new AlreadyExistsException("Group already exists. " +
                    "Trainer: " + group.getTrainer().getUser().getName() +
                    ", Difficulty: " + group.getDifficulty());
        }
    }

    @Override
    public Group updateGroup(Group group) {
        if (
                group.getDifficulty() == null ||
                        group.getTrainer() == null
        ) {
            throw new ResourceNotFoundException("Incorrect JSON. All fields must be filled " +
                    "(trainerId, difficulty).");
        }

        if (group.getStudents() != null) {
            for (Student student : group.getStudents()) {
                if (!studentRepository.existsById(student.getId())) {
                    throw new ResourceNotFoundException("Incorrect Student ID. Student ID: " +
                            student.getId());
                }
            }
        }

        Optional<Group> searchedGroup = groupRepository.findByTrainerAndDifficulty(
                group.getTrainer(),
                group.getDifficulty()
        );

        if (searchedGroup.isPresent() && !group.getId().equals(searchedGroup.get().getId())) {
            throw new AlreadyExistsException("Group already exists. " +
                    "TrainerId: " + group.getTrainer().getId() +
                    ", Difficulty: " + group.getDifficulty());
        }

        Group updatedGroup;

        if (groupRepository.findById(group.getId()).isPresent()) {
            updatedGroup = groupRepository.save(group);
        } else {
            throw new ResourceNotFoundException("The group does not exist." +
                    " ID: " + group.getId() +
                    ", Trainer: " + group.getTrainer().getUser().getName() +
                    ", Difficulty: " + group.getDifficulty());
        }
        cacheConfig.putGroup(group.getId(), updatedGroup);

        return updatedGroup;
    }

    @Override
    public void deleteGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found. ID: " + id));

        cacheConfig.removeGroup(id);
        groupRepository.delete(group);
    }
}
