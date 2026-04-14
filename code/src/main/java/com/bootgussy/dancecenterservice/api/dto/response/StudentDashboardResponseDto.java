package com.bootgussy.dancecenterservice.api.dto.response;

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
public class StudentDashboardResponseDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private List<GroupResponseDto> groups;
}
