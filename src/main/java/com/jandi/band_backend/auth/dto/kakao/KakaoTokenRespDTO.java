package com.jandi.band_backend.auth.dto.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "카카오 토큰 응답 DTO")
@Data
@AllArgsConstructor
public class KakaoTokenRespDTO {
    @Schema(description = "카카오 액세스 토큰", example = "xxxxxx_kakao_access_token_xxxxxx")
    private String accessToken;
    
    @Schema(description = "카카오 리프레시 토큰", example = "xxxxxx_kakao_refresh_token_xxxxxx")
    private String refreshToken;
    
    @Schema(description = "토큰 만료 시간 (초)", example = "3600")
    private int expiresIn;
}
