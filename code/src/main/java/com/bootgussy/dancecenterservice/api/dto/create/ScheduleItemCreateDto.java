
package com.bootgussy.dancecenterservice.api.dto.create;

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
public class ScheduleItemCreateDto {
    private Long hallId;

    private Long groupId;

    private String dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;
}
