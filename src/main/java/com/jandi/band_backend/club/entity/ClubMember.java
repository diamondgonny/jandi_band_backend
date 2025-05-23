package com.jandi.band_backend.club.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "동아리 멤버 엔티티")
@Entity
@Table(name = "club_member", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"club_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class ClubMember {

    @Schema(description = "동아리 구성원 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_member_id")
    private Integer id;

    @Schema(description = "소속 동아리")
    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Schema(description = "구성원 사용자")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Schema(description = "동아리 내 역할(대표/구성원)", example = "MEMBER")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role = MemberRole.MEMBER;

    @Schema(description = "가입 시각", example = "2024-01-01T00:00:00")
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Schema(description = "수정 시각", example = "2024-01-01T00:00:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Schema(description = "동아리 멤버 역할")
    public enum MemberRole {
        @Schema(description = "대표")
        REPRESENTATIVE, 
        @Schema(description = "일반 멤버")
        MEMBER
    }
}
