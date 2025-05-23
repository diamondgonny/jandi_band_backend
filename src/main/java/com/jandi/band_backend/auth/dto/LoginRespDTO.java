package com.jandi.band_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "로그인 응답")
@Getter
public class LoginRespDTO extends TokenRespDTO{
    @Schema(description = "회원가입 여부 (true: 기존 회원, false: 신규 회원)")
    private final Boolean isRegistered;

    public LoginRespDTO(String accessToken, String refreshToken, Boolean isRegistered) {
        super(accessToken, refreshToken);
        this.isRegistered = isRegistered;
    }
}
