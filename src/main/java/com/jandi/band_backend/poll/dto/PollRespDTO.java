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
@Schema(description = "투표 응답 DTO")
public class PollRespDTO {
    @Schema(description = "투표 ID", example = "1")
    private Integer id;
    
    @Schema(description = "투표 제목", example = "다음 공연 곡 선정")
    private String title;
    
    @Schema(description = "동아리 ID", example = "1")
    private Integer clubId;
    
    @Schema(description = "동아리 이름", example = "서울대 록밴드")
    private String clubName;
    
    @Schema(description = "투표 시작 시간", example = "2024-01-01T10:00:00")
    private LocalDateTime startDatetime;
    
    @Schema(description = "투표 마감 시간", example = "2024-12-31T23:59:59")
    private LocalDateTime endDatetime;
    
    @Schema(description = "투표 생성자 ID", example = "1")
    private Integer creatorId;
    
    @Schema(description = "투표 생성자 이름", example = "김철수")
    private String creatorName;
    
    @Schema(description = "투표 생성일시", example = "2024-01-01T09:00:00")
    private LocalDateTime createdAt;
}
