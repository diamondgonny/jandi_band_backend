package com.jandi.band_backend.notice.dto;

import com.jandi.band_backend.notice.entity.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "공지사항 응답 DTO")
public class NoticeRespDTO {

    @Schema(description = "공지사항 ID", example = "1")
    private Integer id;

    @Schema(description = "공지사항 제목", example = "사이트 점검 안내")
    private String title;

    @Schema(description = "공지사항 내용", example = "오늘 밤 12시부터 새벽 2시까지 사이트 점검이 있습니다.")
    private String content;

    @Schema(description = "팝업 노출 시작 시각", example = "2024-12-10T00:00:00")
    private LocalDateTime startDatetime;

    @Schema(description = "팝업 노출 종료 시각", example = "2024-12-10T23:59:59")
    private LocalDateTime endDatetime;

    @Schema(description = "일시정지 여부", example = "false")
    private Boolean isPaused;

    public NoticeRespDTO(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.startDatetime = notice.getStartDatetime();
        this.endDatetime = notice.getEndDatetime();
        this.isPaused = notice.getIsPaused();
    }
}
