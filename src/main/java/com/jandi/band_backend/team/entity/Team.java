package com.jandi.band_backend.team.entity;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.poll.entity.Poll;
import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "팀 엔티티")
@Entity
@Table(name = "team")
@Getter
@Setter
@NoArgsConstructor
public class Team {
    
    @Schema(description = "팀 고유 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Integer id;
    
    @Schema(description = "소속 동아리")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
    
    @Schema(description = "팀 이름", example = "레드썬더")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Schema(description = "팀 설명", example = "록음악을 연주하는 팀입니다.")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Schema(description = "팀 생성자")
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
    
    @Schema(description = "팀 멤버 목록")
    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMembers = new ArrayList<>();
    
    @Schema(description = "팀 이벤트 목록")
    @OneToMany(mappedBy = "team")
    private List<TeamEvent> teamEvents = new ArrayList<>();
    
    @Schema(description = "팀 투표 목록")
    @OneToMany(mappedBy = "team")
    private List<Poll> polls = new ArrayList<>();
    
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
