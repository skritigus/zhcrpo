package com.bootgussy.dancecenterservice.api.dto.response;

import com.bootgussy.dancecenterservice.core.model.ScheduleItem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HallResponseDto {
    private Long id;

    private String name;

    private Integer area;

    private List<Long> scheduleItemsId;
}
