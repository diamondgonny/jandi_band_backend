package com.jandi.band_backend.team.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "팀 구성원 엔티티")
@Entity
@Table(name = "team_member")
@Getter
@Setter
@NoArgsConstructor
public class TeamMember {
    
    @Schema(description = "팀 구성원 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_member_id")
    private Integer id;
    
    @Schema(description = "소속 팀")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    
    @Schema(description = "구성원 사용자 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    
    @Schema(description = "팀 합류 시각", example = "2024-01-01T00:00:00")
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
} 
