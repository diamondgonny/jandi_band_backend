package com.jandi.band_backend.auth.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoUserInfoDTO {
    private String kakaoOauthId;
    private String nickname;
    private String profilePhoto;
}
