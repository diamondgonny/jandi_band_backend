package com.jandi.band_backend.auth.dto.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "카카오 사용자 정보")
@Data
@AllArgsConstructor
public class KakaoUserInfoDTO {
    @Schema(description = "카카오 OAuth ID")
    private String kakaoOauthId;
    
    @Schema(description = "카카오 닉네임")
    private String nickname;
    
    @Schema(description = "카카오 프로필 사진 URL")
    private String profilePhoto;
}
