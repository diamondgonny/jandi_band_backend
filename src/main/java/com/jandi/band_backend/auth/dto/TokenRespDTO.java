package com.jandi.band_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "토큰 응답")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRespDTO {
    @Schema(description = "액세스 토큰")
    private String accessToken;
    
    @Schema(description = "리프레시 토큰")
    private String refreshToken;
}
