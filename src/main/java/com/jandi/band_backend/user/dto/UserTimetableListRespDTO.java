package com.jandi.band_backend.user.dto;

import com.jandi.band_backend.user.entity.UserTimetable;
import lombok.Data;

@Data
public class UserTimetableListRespDTO {
    private Integer id;
    private String name;

    public UserTimetableListRespDTO(UserTimetable userTimetable) {
        this.id=userTimetable.getId();
        this.name=userTimetable.getName();
    }
}
