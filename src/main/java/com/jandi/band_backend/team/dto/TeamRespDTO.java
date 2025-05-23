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
@Schema(description = "팀 응답 DTO")
public class TeamRespDTO {

    @Schema(description = "팀 ID", example = "1")
    private Integer id;
    
    @Schema(description = "팀 이름", example = "록밴드 팀")
    private String name;
    
    @Schema(description = "팀 생성자 ID", example = "1")
    private Integer creatorId;
    
    @Schema(description = "팀 생성자 이름", example = "김철수")
    private String creatorName;
    
    @Schema(description = "팀 멤버 수", example = "5")
    private Integer memberCount;
    
    @Schema(description = "팀 생성일시", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
