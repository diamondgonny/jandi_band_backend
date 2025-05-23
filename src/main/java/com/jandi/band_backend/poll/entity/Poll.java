package com.jandi.band_backend.poll.entity;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.team.entity.Team;
import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "투표 엔티티")
@Entity
@Table(name = "poll")
@Getter
@Setter
@NoArgsConstructor
public class Poll {
    
    @Schema(description = "투표 폼 고유 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poll_id")
    private Integer id;
    
    @Schema(description = "소속 동아리")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
    
    @Schema(description = "특정 팀 한정 투표 시 팀 ID (전체 동아리 투표면 NULL)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    
    @Schema(description = "투표 제목", example = "12월 정기공연 세트리스트 투표")
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Schema(description = "투표 시작 일시", example = "2024-01-01T00:00:00")
    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;
    
    @Schema(description = "투표 종료 일시", example = "2024-01-07T23:59:59")
    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;
    
    @Schema(description = "투표 생성자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id", nullable = false)
    private Users creator;
    
    @Schema(description = "생성 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시각", example = "2024-01-01T00:00:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Schema(description = "삭제 시각", example = "2024-01-01T00:00:00")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Schema(description = "투표 후보 곡 목록")
    @OneToMany(mappedBy = "poll")
    private List<PollSong> songs = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 
