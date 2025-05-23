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
@Schema(description = "동아리 응답")
public class ClubRespDTO {
    @Schema(description = "동아리 ID")
    private Integer id;
    
    @Schema(description = "동아리 이름")
    private String name;
    
    @Schema(description = "소속 대학명")
    private String universityName;
    
    @Schema(description = "연합 동아리 여부")
    private Boolean isUnionClub;
    
    @Schema(description = "동아리 사진 URL")
    private String photoUrl;
    
    @Schema(description = "동아리 멤버 수")
    private Integer memberCount;
}
