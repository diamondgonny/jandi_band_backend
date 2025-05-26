package com.jandi.band_backend.team.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TimetableReqDTO {

    @NotNull(message = "사용자 시간표 ID는 필수입니다.")
    private Integer userTimetableId;
}
