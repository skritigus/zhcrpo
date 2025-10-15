package com.bootgussy.dancecenterservice.core.mapper;

import com.bootgussy.dancecenterservice.api.dto.create.TrainerCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.TrainerResponseDto;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.Trainer;
import com.bootgussy.dancecenterservice.core.repository.GroupRepository;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TrainerMapper {
    @Autowired
    protected GroupRepository groupRepository;

    public abstract Trainer toEntity(TrainerCreateDto trainerCreateDto);

    @Mapping(target = "groupsId",
            expression = "java(trainer.getGroups() != null " +
                    "? " +
                    "trainer.getGroups().stream().map(g -> g.getId()).toList() " +
                    ": " +
                    "new ArrayList<>())")
    public abstract TrainerResponseDto toResponseDto(Trainer trainer);

    public abstract List<TrainerResponseDto> toResponseDtoList(List<Trainer> trainers);
}
