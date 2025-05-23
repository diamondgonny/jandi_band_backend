package com.jandi.band_backend.user.dto;

import com.jandi.band_backend.user.entity.UserTimetable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "사용자 시간표 응답 DTO")
public class UserTimetableRespDTO {
    @Schema(description = "시간표 ID", example = "1")
    private Integer id;
    
    @Schema(description = "시간표 이름", example = "2024년 1학기 시간표")
    private String name;

    public UserTimetableRespDTO(UserTimetable userTimetable) {
        this.id=userTimetable.getId();
        this.name=userTimetable.getName();
    }
}
