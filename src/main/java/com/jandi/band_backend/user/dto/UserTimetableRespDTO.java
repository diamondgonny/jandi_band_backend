package com.jandi.band_backend.user.dto;

import com.jandi.band_backend.user.entity.UserTimetable;
import lombok.Data;

@Data
public class UserTimetableRespDTO {
    private Integer id;
    private String name;

    public UserTimetableRespDTO(UserTimetable userTimetable) {
        this.id=userTimetable.getId();
        this.name=userTimetable.getName();
    }
}
