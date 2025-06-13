package com.jandi.band_backend.promo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "공연 홍보 신고 요청 DTO")
public class PromoReportReqDTO {

    @Schema(description = "신고할 공연 홍보 ID", example = "1", required = true)
    @NotNull(message = "공연 홍보 ID는 필수입니다")
    private Integer promoId;

    @Schema(description = "신고 이유 ID", example = "3", required = true)
    @NotNull(message = "신고 이유 ID는 필수입니다")
    private Integer reportReasonId;

    @Schema(description = "신고 상세 설명", example = "부적절한 내용이 포함되어 있습니다.")
    private String description;
}
