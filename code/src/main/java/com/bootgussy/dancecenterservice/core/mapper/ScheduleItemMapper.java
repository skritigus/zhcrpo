package com.bootgussy.dancecenterservice.core.mapper;

import com.bootgussy.dancecenterservice.api.dto.create.ScheduleItemCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.ScheduleItemResponseDto;
import com.bootgussy.dancecenterservice.core.model.Group;
import com.bootgussy.dancecenterservice.core.model.Hall;
import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import com.bootgussy.dancecenterservice.core.repository.GroupRepository;
import com.bootgussy.dancecenterservice.core.repository.HallRepository;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ScheduleItemMapper {
    @Autowired
    protected GroupRepository groupRepository;

    @Autowired
    protected HallRepository hallRepository;

    @Autowired
    protected HallMapper hallMapper;

    @Autowired
    protected GroupMapper groupMapper;

    @Mapping(target = "hall",
            expression = "java(mapHallIdToHall(scheduleItemCreateDto.getHallId()))")
    @Mapping(target = "group",
            expression = "java(mapGroupIdToGroup(scheduleItemCreateDto.getGroupId()))")
    public abstract ScheduleItem toEntity(ScheduleItemCreateDto scheduleItemCreateDto);

    @Mapping(target = "hall",
            expression = "java(hallMapper.toResponseDto(scheduleItem.getHall()))")
    @Mapping(target = "group",
            expression = "java(groupMapper.toResponseDto(scheduleItem.getGroup()))")
    public abstract ScheduleItemResponseDto toResponseDto(ScheduleItem scheduleItem);

    public abstract List<ScheduleItemResponseDto> toResponseDtoList(List<ScheduleItem> scheduleItems);

    protected Group mapGroupIdToGroup(Long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    protected Hall mapHallIdToHall(Long hallId) {
        return hallRepository.findById(hallId).orElse(null);
    }
}
