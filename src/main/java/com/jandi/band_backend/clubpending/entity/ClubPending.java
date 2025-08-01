package com.jandi.band_backend.clubpending.entity;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.user.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "club_pending", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"club_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class ClubPending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_pending_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PendingStatus status = PendingStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "processed_by")
    private Users processedBy; // 승인/거부한 관리자

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // 신청 만료일

    public enum PendingStatus {
        PENDING,    // 승인 대기
        APPROVED,   // 승인됨
        REJECTED,   // 거부됨
        EXPIRED     // 만료됨
    }

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
        // 7일 후 만료 (설정 가능)
        expiresAt = LocalDateTime.now().plusDays(7);
    }
}