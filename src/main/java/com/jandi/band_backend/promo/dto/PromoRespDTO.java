package com.jandi.band_backend.promo.dto;

import com.jandi.band_backend.promo.entity.Promo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Schema(description = "공연 홍보 응답 DTO")
public class PromoRespDTO {
    
    @Schema(description = "공연 홍보 ID", example = "1")
    private Integer id;
    
    @Schema(description = "팀명", example = "락밴드 팀")
    private String teamName;
    
    @Schema(description = "작성자 ID", example = "1")
    private Integer creatorId;
    
    @Schema(description = "작성자명", example = "홍길동")
    private String creatorName;
    
    @Schema(description = "공연 제목", example = "락밴드 동아리 정기공연")
    private String title;
    
    @Schema(description = "입장료 (원)", example = "10000")
    private BigDecimal admissionFee;
    
    @Schema(description = "공연 일시", example = "2024-03-15T19:00:00")
    private LocalDateTime eventDatetime;
    
    @Schema(description = "공연 장소명", example = "홍대 클럽")
    private String location;
    
    @Schema(description = "상세 주소", example = "서울시 마포구 홍익로 123")
    private String address;
    
    @Schema(description = "공연 설명", example = "락밴드 동아리의 정기 공연입니다.")
    private String description;
    
    @Schema(description = "조회수", example = "100")
    private Integer viewCount;
    
    @Schema(description = "댓글 수", example = "5")
    private Integer commentCount;
    
    @Schema(description = "좋아요 수", example = "20")
    private Integer likeCount;
    
    @Schema(description = "현재 사용자의 좋아요 상태 (true: 좋아요 누름, false: 좋아요 안 누름, null: 인증되지 않은 사용자)", example = "true")
    private Boolean isLikedByUser;
    
    @Schema(description = "생성일시", example = "2024-03-01T10:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시", example = "2024-03-01T10:00:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "공연 이미지 URL 목록", example = "[\"https://example.com/photo1.jpg\", \"https://example.com/photo2.jpg\"]")
    private List<String> photoUrls;

    public static PromoRespDTO from(Promo promo) {
        PromoRespDTO response = new PromoRespDTO();
        response.setId(promo.getId());
        response.setTeamName(promo.getTeamName());
        response.setCreatorId(promo.getCreator().getId());
        response.setCreatorName(promo.getCreator().getNickname());
        response.setTitle(promo.getTitle());
        response.setAdmissionFee(promo.getAdmissionFee());
        response.setEventDatetime(promo.getEventDatetime());
        response.setLocation(promo.getLocation());
        response.setAddress(promo.getAddress());
        response.setDescription(promo.getDescription());
        response.setViewCount(promo.getViewCount());
        response.setCommentCount(promo.getCommentCount());
        response.setLikeCount(promo.getLikeCount());
        response.setIsLikedByUser(null); // 기본값은 null (인증되지 않은 사용자)
        response.setCreatedAt(promo.getCreatedAt());
        response.setUpdatedAt(promo.getUpdatedAt());
        response.setPhotoUrls(promo.getPhotos().stream()
                .filter(photo -> photo.getDeletedAt() == null)  // 삭제되지 않은 사진만 포함
                .map(photo -> photo.getImageUrl())
                .collect(Collectors.toList()));
        return response;
    }

    public static PromoRespDTO from(Promo promo, Boolean isLikedByUser) {
        PromoRespDTO response = from(promo);
        response.setIsLikedByUser(isLikedByUser);
        return response;
    }
} 