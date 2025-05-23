package com.jandi.band_backend.user.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "사용자 시간표 생성/수정 요청 DTO")
public class UserTimetableReqDTO {
    
    @Schema(description = "시간표 이름", example = "2024년 1학기 시간표", required = true)
    private String name;
    
    @Schema(description = "시간표 데이터 (JSON 형태)", 
            example = "{ \"monday\": [{\"time\": \"09:00-10:30\", \"subject\": \"수학\"}], \"tuesday\": [] }", 
            required = true)
    private JsonNode timetableData;
}
