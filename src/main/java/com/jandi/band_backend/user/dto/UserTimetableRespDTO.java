package com.jandi.band_backend.user.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTimetableRespDTO {
    private Integer id;
    private String name;
    private JsonNode timetableData;
}
