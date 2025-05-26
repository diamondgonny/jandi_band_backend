package com.jandi.band_backend.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "페이지네이션 응답 DTO")
public class PagedRespDTO<T> {
    
    @Schema(description = "실제 데이터 목록")
    private List<T> content;
    
    @Schema(description = "페이지 정보")
    private PageInfo pageInfo;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "페이지 정보")
    public static class PageInfo {
        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        private int page;
        
        @Schema(description = "페이지 크기", example = "20")
        private int size;
        
        @Schema(description = "총 요소 수", example = "100")
        private long totalElements;
        
        @Schema(description = "총 페이지 수", example = "5")
        private int totalPages;
        
        @Schema(description = "첫 번째 페이지 여부", example = "true")
        private boolean first;
        
        @Schema(description = "마지막 페이지 여부", example = "false")
        private boolean last;
        
        @Schema(description = "비어있는 페이지 여부", example = "false")
        private boolean empty;
    }
    
    /**
     * Spring Data Page 객체를 PagedRespDTO로 변환
     */
    public static <T> PagedRespDTO<T> from(Page<T> page) {
        return PagedRespDTO.<T>builder()
                .content(page.getContent())
                .pageInfo(PageInfo.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .first(page.isFirst())
                        .last(page.isLast())
                        .empty(page.isEmpty())
                        .build())
                .build();
    }
} 