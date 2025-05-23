package com.jandi.band_backend.user.dto;

import com.jandi.band_backend.user.entity.UserTimetable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "사용자 시간표 응답")
public class UserTimetableRespDTO {
    @Schema(description = "시간표 ID")
    private Integer id;
    
    @Schema(description = "시간표 이름")
    private String name;

    public UserTimetableRespDTO(UserTimetable userTimetable) {
        this.id=userTimetable.getId();
        this.name=userTimetable.getName();
    }
}
