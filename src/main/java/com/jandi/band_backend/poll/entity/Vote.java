package com.jandi.band_backend.poll.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "투표 엔티티")
@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor
public class Vote {
    
    @Schema(description = "개별 투표 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Integer id;
    
    @Schema(description = "대상 곡")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_song_id", nullable = false)
    private PollSong pollSong;
    
    @Schema(description = "투표자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    
    @Schema(description = "선택한 의견", example = "LIKE")
    @Enumerated(EnumType.STRING)
    @Column(name = "voted_mark", nullable = false)
    private VotedMark votedMark;
    
    @Schema(description = "투표 일시", example = "2024-01-01T00:00:00")
    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;
    
    @PrePersist
    protected void onCreate() {
        votedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        votedAt = LocalDateTime.now();
    }

    @Schema(description = "투표 의견 표시")
    public enum VotedMark {
        @Schema(description = "좋아요")
        LIKE, 
        @Schema(description = "싫어요")
        DISLIKE, 
        @Schema(description = "할 수 없음")
        CANT, 
        @Schema(description = "하고 싶음")
        HAJJ
    }
}
