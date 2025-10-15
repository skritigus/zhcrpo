package com.bootgussy.dancecenterservice.core.mapper;

import com.bootgussy.dancecenterservice.api.dto.create.HallCreateDto;
import com.bootgussy.dancecenterservice.api.dto.response.HallResponseDto;
import com.bootgussy.dancecenterservice.core.model.Hall;
import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import com.bootgussy.dancecenterservice.core.repository.ScheduleItemRepository;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class HallMapper {
    @Autowired
    protected ScheduleItemRepository scheduleItemRepository;

    public abstract Hall toEntity(HallCreateDto hallCreateDto);

    @Mapping(target = "scheduleItemsId",
            expression = "java(hall.getScheduleItems() != null " +
                    "? " +
                    "hall.getScheduleItems().stream().map(s -> s.getId()).toList() " +
                    ": " +
                    "new ArrayList<>())")
    public abstract HallResponseDto toResponseDto(Hall hall);

    public abstract List<HallResponseDto> toResponseDtoList(List<Hall> halls);
}
