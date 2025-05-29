package com.jandi.band_backend.promo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "공연 홍보 간단 응답 DTO")
public class PromoSimpleRespDTO {
    
    @Schema(description = "공연 홍보 ID", example = "1")
    private Integer id;
    
    public PromoSimpleRespDTO(Integer id) {
        this.id = id;
    }
    
    public static PromoSimpleRespDTO of(Integer id) {
        return new PromoSimpleRespDTO(id);
    }
} 