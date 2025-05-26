package com.jandi.band_backend.team.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class TimetableReqDTO {

    @NotNull(message = "시간표 데이터는 필수입니다.")
    private Map<String, List<String>> timetableData;
}
