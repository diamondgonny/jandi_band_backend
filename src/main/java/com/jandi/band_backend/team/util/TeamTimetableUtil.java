package com.jandi.band_backend.team.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.jandi.band_backend.global.util.TimetableValidationUtil;
import com.jandi.band_backend.team.dto.TimetableUpdateReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamTimetableUtil {
    private final TimetableValidationUtil timetableValidationUtil;

    // String 형식의 시간표 데이터를 JSON으로 변환
    public JsonNode stringToJson(String stringData) {
        return timetableValidationUtil.stringToJson(stringData);
    }

    // TimetableUpdateReqDTO 검사
    public void validateTimetableRequest(TimetableUpdateReqDTO requestDTO) {
        // DTO 검증
        JsonNode timetableData = requestDTO.getTimetableData();

        // 시간표 형식 검증 (공통 유틸리티 사용)
        timetableValidationUtil.validateTimetableData(timetableData);
    }

    // JsonNode 시간표 데이터 직접 검사 (Team 도메인 전용)
    public void validateTimetableData(JsonNode timetableData) {
        // 시간표 형식 검증 (공통 유틸리티 사용)
        timetableValidationUtil.validateTimetableData(timetableData);
    }
} 