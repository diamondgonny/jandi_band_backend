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
@Schema(description = "동아리 생성 요청 DTO")
public class ClubReqDTO {
    @Schema(description = "동아리 이름", example = "서울대 록밴드", required = true, maxLength = 100)
    @NotBlank(message = "동아리 이름은 필수입니다")
    @Size(max = 100, message = "동아리 이름은 100자 이내여야 합니다")
    private String name;
    // universityId가 null이면 연합동아리, 아니면 특정 대학 소속 동아리
    @Schema(description = "대학교 ID (null이면 연합동아리, 값이 있으면 특정 대학 소속 동아리)", example = "1", required = false)
    private Integer universityId;
    @Schema(description = "카카오톡 채팅방 링크", example = "https://open.kakao.com/o/gABC123", required = false, maxLength = 255)
    @Size(max = 255, message = "카카오톡 채팅방 링크는 255자 이내여야 합니다")
    private String chatroomUrl;
    @Schema(description = "동아리 설명", example = "서울대학교 록밴드 동아리입니다.", required = false)
    private String description;
    @Schema(description = "인스타그램 아이디", example = "seoul_rockband", required = false, maxLength = 50)
    @Size(max = 50, message = "인스타그램 아이디는 50자 이내여야 합니다")
    private String instagramId;
    @Schema(description = "동아리 사진 URL", example = "https://example.com/photo.jpg", required = false)
    private String photoUrl;
}
