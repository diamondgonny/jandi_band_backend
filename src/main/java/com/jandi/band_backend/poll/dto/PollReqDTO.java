package com.jandi.band_backend.poll.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "투표 생성 요청 DTO")
public class PollReqDTO {
    @Schema(description = "투표 제목", example = "다음 공연 곡 선정", required = true)
    @NotBlank(message = "투표 제목은 필수입니다.")
    private String title;
    
    @Schema(description = "동아리 ID", example = "1", required = true)
    @NotNull(message = "동아리 ID는 필수입니다.")
    private Integer clubId;
    
    @Schema(description = "투표 마감 시간", example = "2024-12-31T23:59:59", required = true)
    @NotNull(message = "투표 마감 시간은 필수입니다.")
    @Future(message = "투표 마감 시간은 현재 시간 이후로 설정해야 합니다.")
    private LocalDateTime endDatetime;
}
