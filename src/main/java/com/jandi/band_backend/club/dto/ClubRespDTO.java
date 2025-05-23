package com.jandi.band_backend.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "동아리 응답 DTO")
public class ClubRespDTO {
    @Schema(description = "동아리 ID", example = "1")
    private Integer id;
    
    @Schema(description = "동아리 이름", example = "서울대 록밴드")
    private String name;
    
    @Schema(description = "소속 대학명", example = "서울대학교")
    private String universityName;
    
    @Schema(description = "연합 동아리 여부", example = "false")
    private Boolean isUnionClub;
    
    @Schema(description = "동아리 사진 URL", example = "https://example.com/photo.jpg")
    private String photoUrl;
    
    @Schema(description = "동아리 멤버 수", example = "25")
    private Integer memberCount;
}
