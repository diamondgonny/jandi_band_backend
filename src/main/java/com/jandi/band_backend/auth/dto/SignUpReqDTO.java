package com.jandi.band_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 요청 DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpReqDTO {
    @Schema(description = "사용자 포지션 (악기 또는 역할)", example = "기타", required = true)
    private String position;
    
    @Schema(description = "대학교명", example = "서울대학교", required = true)
    private String university;
}
