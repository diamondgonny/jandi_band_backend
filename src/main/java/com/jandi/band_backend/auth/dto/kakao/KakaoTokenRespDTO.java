package com.jandi.band_backend.auth.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoTokenRespDTO {
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
}
