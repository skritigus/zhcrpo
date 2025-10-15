package com.bootgussy.dancecenterservice.core.mapper;

import com.bootgussy.dancecenterservice.api.dto.create.GroupCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.GroupResponseDto;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import com.bootgussy.dancecenterservice.core.model.Student;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import com.bootgussy.dancecenterservice.core.repository.ScheduleItemRepository;
import com.bootgussy.dancecenterservice.core.repository.StudentRepository;
import com.bootgussy.dancecenterservice.core.repository.TrainerRepository;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class GroupMapper {
    @Autowired
    protected StudentRepository studentRepository;

    @Autowired
    protected TrainerRepository trainerRepository;

    @Autowired
    protected StudentMapper studentMapper;

    @Autowired
    protected TrainerMapper trainerMapper;

    @Mapping(target = "trainer",
            expression = "java(mapTrainerIdToTrainer(groupCreateDto.getTrainerId()))")
    @Mapping(target = "students",
            expression = "java(mapStudentsIdToStudents(groupCreateDto.getStudentsId()))")
    public abstract Group toEntity(GroupCreateDto groupCreateDto);

    @Mapping(target = "scheduleItemsId",
            expression = "java(group.getScheduleItems() != null " +
                    "? " +
                    "group.getScheduleItems().stream().map(s -> s.getId()).toList() " +
                    ": " +
                    "new ArrayList<>())")
    @Mapping(target = "students",
            expression = "java(studentMapper.toResponseDtoList(group.getStudents()))")
    @Mapping(target = "trainer",
            expression = "java(trainerMapper.toResponseDto(group.getTrainer()))")
    public abstract GroupResponseDto toResponseDto(Group group);

    public abstract List<GroupResponseDto> toResponseDtoList(List<Group> entities);

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
