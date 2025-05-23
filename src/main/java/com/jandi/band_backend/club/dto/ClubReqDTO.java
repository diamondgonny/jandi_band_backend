package com.jandi.band_backend.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "동아리 생성 요청")
public class ClubReqDTO {
    @Schema(description = "동아리 이름")
    @NotBlank(message = "동아리 이름은 필수입니다")
    @Size(max = 100, message = "동아리 이름은 100자 이내여야 합니다")
    private String name;
    // universityId가 null이면 연합동아리, 아니면 특정 대학 소속 동아리
    @Schema(description = "대학교 ID")
    private Integer universityId;
    @Schema(description = "카카오톡 채팅방 링크")
    @Size(max = 255, message = "카카오톡 채팅방 링크는 255자 이내여야 합니다")
    private String chatroomUrl;
    @Schema(description = "동아리 설명")
    private String description;
    @Schema(description = "인스타그램 아이디")
    @Size(max = 50, message = "인스타그램 아이디는 50자 이내여야 합니다")
    private String instagramId;
    @Schema(description = "동아리 사진 URL")
    private String photoUrl;
}
