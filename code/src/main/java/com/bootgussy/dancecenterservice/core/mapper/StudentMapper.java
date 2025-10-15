package com.bootgussy.dancecenterservice.core.mapper;

import com.bootgussy.dancecenterservice.api.dto.create.StudentCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.StudentResponseDto;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.Student;
import com.bootgussy.dancecenterservice.core.repository.GroupRepository;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class StudentMapper {
    @Autowired
    protected GroupRepository groupRepository;

    public abstract Student toEntity(StudentCreateDto studentCreateDto);

    @Mapping(target = "groupsId",
            expression = "java(student.getGroups() != null " +
                    "? " +
                    "student.getGroups().stream().map(g -> g.getId()).toList() " +
                    ": " +
                    "new ArrayList<>())")
    public abstract StudentResponseDto toResponseDto(Student student);

    @Mapping(target = "groupsId",
            expression = "java(student.getGroups() != null " +
                    "? " +
                    "student.getGroups().stream().map(g -> g.getId()).toList() " +
                    ": " +
                    "new ArrayList<>())")
    public abstract List<StudentResponseDto> toResponseDtoList(List<Student> students);
}
