package com.jandi.band_backend.promo.dto;

import com.jandi.band_backend.promo.entity.Promo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "홍보 게시글 요청")
public class PromoReqDTO {
    @Schema(description = "클럽 ID")
    @NotNull(message = "클럽 ID는 필수입니다")
    private Integer clubId;

    @Schema(description = "제목")
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    private String title;

    @Schema(description = "입장료")
    private BigDecimal admissionFee;

    @Schema(description = "이벤트 일시")
    private LocalDateTime eventDatetime;

    @Schema(description = "공연 장소")
    @Size(max = 255, message = "장소는 255자를 초과할 수 없습니다")
    private String location;

    @Schema(description = "상세 주소")
    @Size(max = 255, message = "주소는 255자를 초과할 수 없습니다")
    private String address;

    @Schema(description = "내용")
    private String description;

    @Schema(description = "상태")
    private Promo.PromoStatus status;
} 