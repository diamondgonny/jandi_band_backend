package com.jandi.band_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "토큰 재발급 요청")
@Data
@AllArgsConstructor
public class RefreshReqDTO {
    @Schema(description = "리프레시 토큰")
    private String refreshToken;
}
