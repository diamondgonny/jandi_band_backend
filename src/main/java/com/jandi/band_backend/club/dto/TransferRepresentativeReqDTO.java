package com.jandi.band_backend.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "동아리 대표자 위임 요청 DTO")
public class TransferRepresentativeReqDTO {
    @NotNull(message = "위임받을 사용자 ID는 필수입니다")
    @Schema(description = "새로운 대표자로 지정할 사용자 ID", example = "2")
    private Integer newRepresentativeUserId;
}
