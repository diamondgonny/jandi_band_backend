package com.jandi.band_backend.promo.entity;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "공연 홍보 엔티티")
@Entity
@Table(name = "promo")
@Getter
@Setter
@NoArgsConstructor
public class Promo {
    
    @Schema(description = "홍보 게시글 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_id")
    private Integer id;
    
    @Schema(description = "게시글 작성 동아리")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
    
    @Schema(description = "작성자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id", nullable = false)
    private Users creator;
    
    @Schema(description = "게시글 제목", example = "2024 정기공연 '락의 밤'")
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Schema(description = "입장료", example = "15000.00")
    @Column(name = "admission_fee", precision = 10, scale = 2)
    private BigDecimal admissionFee;
    
    @Schema(description = "행사 일시", example = "2024-12-25T19:00:00")
    @Column(name = "event_datetime")
    private LocalDateTime eventDatetime;
    
    @Schema(description = "행사 장소", example = "서울대학교 관악 SK관 컨벤션홀")
    @Column(name = "location", length = 255)
    private String location;
    
    @Schema(description = "상세 주소", example = "서울특별시 관악구 관악로 1")
    @Column(name = "address", length = 255)
    private String address;
    
    @Schema(description = "게시글 내용", example = "올해 정기공연에서 최고의 무대를 선보일 예정입니다!")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Schema(description = "진행 상태", example = "UPCOMING")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PromoStatus status = PromoStatus.UPCOMING;
    
    @Schema(description = "조회수", example = "150")
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
    
    @Schema(description = "댓글 수", example = "25")
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;
    
    @Schema(description = "좋아요 수", example = "42")
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;
    
    @Schema(description = "생성 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시각", example = "2024-01-01T00:00:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Schema(description = "삭제 시각", example = "2024-01-01T00:00:00")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Schema(description = "홍보 사진 목록")
    @OneToMany(mappedBy = "promo")
    private List<PromoPhoto> photos = new ArrayList<>();
    
    @Schema(description = "좋아요 목록")
    @OneToMany(mappedBy = "promo")
    private List<PromoLike> likes = new ArrayList<>();
    
    @Schema(description = "신고 목록")
    @OneToMany(mappedBy = "promo")
    private List<PromoReport> reports = new ArrayList<>();
    
    @Schema(description = "댓글 목록")
    @OneToMany(mappedBy = "promo")
    private List<PromoComment> comments = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Schema(description = "공연 홍보 상태")
    public enum PromoStatus {
        @Schema(description = "예정됨")
        UPCOMING, 
        @Schema(description = "진행중")
        ONGOING, 
        @Schema(description = "완료됨")
        COMPLETED
    }
} 
