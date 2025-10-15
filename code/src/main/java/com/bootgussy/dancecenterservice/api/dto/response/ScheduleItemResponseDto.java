
package com.bootgussy.dancecenterservice.api.dto.response;

import java.time.LocalTime;
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
public class ScheduleItemResponseDto {
    private Long id;

    private HallResponseDto hall;

    private GroupResponseDto group;

    private String dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;
}
