package com.jandi.band_backend.poll.dto;

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
@Schema(description = "투표 응답")
public class PollRespDTO {
    @Schema(description = "투표 ID")
    private Integer id;
    
    @Schema(description = "투표 제목")
    private String title;
    
    @Schema(description = "동아리 ID")
    private Integer clubId;
    
    @Schema(description = "동아리 이름")
    private String clubName;
    
    @Schema(description = "투표 시작 시간")
    private LocalDateTime startDatetime;
    
    @Schema(description = "투표 마감 시간")
    private LocalDateTime endDatetime;
    
    @Schema(description = "투표 생성자 ID")
    private Integer creatorId;
    
    @Schema(description = "투표 생성자 이름")
    private String creatorName;
    
    @Schema(description = "투표 생성일시")
    private LocalDateTime createdAt;
}
