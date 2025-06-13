package com.jandi.band_backend.promo.dto;

import com.jandi.band_backend.promo.entity.PromoCommentReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "공연 홍보 댓글 신고 응답 DTO")
public class PromoCommentReportRespDTO {

    @Schema(description = "댓글 신고 ID", example = "1")
    private Integer id;

    @Schema(description = "공연 홍보 ID", example = "5")
    private Integer promoId;

    @Schema(description = "공연 홍보 제목", example = "밴드 공연 홍보")
    private String promoTitle;

    @Schema(description = "신고된 댓글 ID", example = "15")
    private Integer promoCommentId;

    @Schema(description = "댓글 내용", example = "정말 기대됩니다!")
    private String commentContent;

    @Schema(description = "댓글 작성자 ID", example = "3")
    private Integer commentCreatorId;

    @Schema(description = "댓글 작성자 이름", example = "이영희")
    private String commentCreatorName;

    @Schema(description = "신고한 사용자 ID", example = "5")
    private Integer reporterUserId;

    @Schema(description = "신고한 사용자 이름", example = "김철수")
    private String reporterUserName;

    @Schema(description = "신고 이유 ID", example = "3")
    private Integer reportReasonId;

    @Schema(description = "신고 이유 코드", example = "HARASSMENT")
    private String reportReasonCode;

    @Schema(description = "신고 상세 설명", example = "부적절한 내용이 포함되어 있습니다.")
    private String description;

    @Schema(description = "신고 생성일시", example = "2024-03-01T15:00:00")
    private LocalDateTime createdAt;

    public static PromoCommentReportRespDTO fromEntity(PromoCommentReport promoCommentReport) {
        PromoCommentReportRespDTO response = new PromoCommentReportRespDTO();
        response.setId(promoCommentReport.getId());
        response.setPromoId(promoCommentReport.getPromoComment().getPromo().getId());
        response.setPromoTitle(promoCommentReport.getPromoComment().getPromo().getTitle());
        response.setPromoCommentId(promoCommentReport.getPromoComment().getId());
        response.setCommentContent(promoCommentReport.getPromoComment().getDescription());
        response.setCommentCreatorId(promoCommentReport.getPromoComment().getCreator().getId());
        response.setCommentCreatorName(promoCommentReport.getPromoComment().getCreator().getNickname());
        response.setReporterUserId(promoCommentReport.getReporter().getId());
        response.setReporterUserName(promoCommentReport.getReporter().getNickname());
        response.setReportReasonId(promoCommentReport.getReportReason().getId());
        response.setReportReasonCode(promoCommentReport.getReportReason().getCode());
        response.setDescription(promoCommentReport.getDescription());
        response.setCreatedAt(promoCommentReport.getCreatedAt());
        return response;
    }
}
