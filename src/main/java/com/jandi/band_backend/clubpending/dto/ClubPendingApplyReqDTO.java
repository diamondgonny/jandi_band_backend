package com.jandi.band_backend.clubpending.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubPendingApplyReqDTO {
    
    @NotNull(message = "동아리 ID는 필수입니다.")
    private Integer clubId;
}