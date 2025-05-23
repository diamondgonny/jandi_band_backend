package com.jandi.band_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 요청")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpReqDTO {
    @Schema(description = "사용자 포지션 (악기 또는 역할)")
    private String position;
    
    @Schema(description = "대학교명")
    private String university;
}
