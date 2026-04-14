package com.bootgussy.dancecenterservice.core.mapper;

import com.bootgussy.dancecenterservice.api.dto.create.GroupCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.GroupResponseDto;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.Student;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import com.bootgussy.dancecenterservice.core.repository.StudentRepository;
import com.bootgussy.dancecenterservice.core.repository.TrainerRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.mapstruct.Named;
import org.mapstruct.IterableMapping;


@Mapper(componentModel = "spring")
public abstract class GroupMapper {
    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    protected GroupMapper groupMapper; // To allow calling shallow mapper from deep mapper if needed
    @Autowired
    protected StudentRepository studentRepository;

    @Autowired
    protected TrainerRepository trainerRepository;

    @Autowired
    @org.springframework.context.annotation.Lazy
    protected StudentMapper studentMapper;

    @Autowired
    @org.springframework.context.annotation.Lazy
    protected TrainerMapper trainerMapper;

    @Autowired
    protected ScheduleItemMapper scheduleItemMapper;

    @Mapping(target = "trainer",
            expression = "java(mapTrainerIdToTrainer(groupCreateDto.getTrainerId()))")
    @Mapping(target = "students",
            expression = "java(mapStudentsIdToStudents(groupCreateDto.getStudentsId()))")
    public abstract Group toEntity(GroupCreateDto groupCreateDto);

    @Named("full")
    @Mapping(target = "scheduleItemsId",
            expression = "java(group.getScheduleItems() != null " +
                    "? " +
                    "group.getScheduleItems().stream().map(s -> s.getId()).toList() " +
                    ": " +
                    "new java.util.ArrayList<>())")
    @Mapping(target = "students",
            expression = "java(studentMapper.toResponseDtoList(group.getStudents()))")
    @Mapping(target = "trainer",
            expression = "java(trainerMapper.toResponseDto(group.getTrainer()))")
    @Mapping(target = "scheduleItems",
            expression = "java(scheduleItemMapper.toResponseDtoList(group.getScheduleItems()))")
    public abstract GroupResponseDto toResponseDto(Group group);

    @IterableMapping(qualifiedByName = "full")
    public abstract List<GroupResponseDto> toResponseDtoList(List<Group> entities);

    @Named("shallow")
    @Mapping(target = "scheduleItemsId", ignore = true)
    @Mapping(target = "scheduleItems", ignore = true)
    @Mapping(target = "students", expression = "java(studentMapper.toResponseDtoList(group.getStudents()))")
    @Mapping(target = "trainer", expression = "java(trainerMapper.toResponseDto(group.getTrainer()))")
    public abstract GroupResponseDto toShallowResponseDto(Group group);

    protected Trainer mapTrainerIdToTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId).orElse(null);
    }

    protected List<Student> mapStudentsIdToStudents(List<Long> studentsId) {
        return studentsId
                .stream()
                .map(id -> studentRepository.findById(id).orElse(null))
                .toList();
    }
}
