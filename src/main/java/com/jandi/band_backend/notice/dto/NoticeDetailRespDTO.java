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
@Schema(description = "공지사항 상세 응답 DTO")
public class NoticeDetailRespDTO {

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

    @Schema(description = "작성자 ID", example = "1")
    private Integer creatorId;

    @Schema(description = "작성자명", example = "관리자")
    private String creatorName;

    @Schema(description = "생성일시", example = "2024-12-09T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-12-09T15:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시 (관리자용)", example = "null")
    private LocalDateTime deletedAt;

    public NoticeDetailRespDTO(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.startDatetime = notice.getStartDatetime();
        this.endDatetime = notice.getEndDatetime();
        this.isPaused = notice.getIsPaused();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
        this.deletedAt = notice.getDeletedAt();
        // NPE 방지
        if (notice.getCreator() != null) {
            this.creatorId = notice.getCreator().getId();
            this.creatorName = notice.getCreator().getNickname();
        }
    }
}
