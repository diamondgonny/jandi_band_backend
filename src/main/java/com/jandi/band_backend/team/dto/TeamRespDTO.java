package com.jandi.band_backend.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "팀 응답")
public class TeamRespDTO {

    @Schema(description = "팀 ID")
    private Integer id;
    
    @Schema(description = "팀 이름")
    private String name;
    
    @Schema(description = "팀 생성자 ID")
    private Integer creatorId;
    
    @Schema(description = "팀 생성자 이름")
    private String creatorName;
    
    @Schema(description = "팀 멤버 수")
    private Integer memberCount;
    
    @Schema(description = "팀 생성일시")
    private LocalDateTime createdAt;
}
