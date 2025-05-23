package com.jandi.band_backend.user.entity;

import com.jandi.band_backend.club.entity.ClubMember;
import com.jandi.band_backend.club.entity.ClubEventParticipant;
import com.jandi.band_backend.club.entity.ClubGalPhoto;
import com.jandi.band_backend.club.entity.ClubEvent;
import com.jandi.band_backend.team.entity.TeamMember;
import com.jandi.band_backend.team.entity.TeamEventParticipant;
import com.jandi.band_backend.team.entity.TeamEvent;
import com.jandi.band_backend.poll.entity.Poll;
import com.jandi.band_backend.poll.entity.PollSong;
import com.jandi.band_backend.poll.entity.Vote;
import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.entity.PromoPhoto;
import com.jandi.band_backend.promo.entity.PromoLike;
import com.jandi.band_backend.promo.entity.PromoReport;
import com.jandi.band_backend.promo.entity.PromoComment;
import com.jandi.band_backend.promo.entity.PromoCommentLike;
import com.jandi.band_backend.promo.entity.PromoCommentReport;
import com.jandi.band_backend.univ.entity.University;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "사용자 엔티티 - 서비스 내 사용자 정보를 관리하는 테이블")
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class Users {
    
    @Schema(description = "사용자 고유 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    
    @Schema(description = "카카오 OAuth 인증 과정에서 제공되는 고유 ID (소셜 로그인 식별자)", example = "1234567890")
    @Column(name = "kakao_oauth_id", nullable = false, unique = true, length = 255)
    private String kakaoOauthId;
    
    @Schema(description = "서비스 내 표시될 별명", example = "록스타김철수")
    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;
    
    @Schema(description = "악기/보컬 포지션", example = "GUITAR")
    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private Position position;
    
    @Schema(description = "사용자가 소속된 대학")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;
    
    @Schema(description = "시스템 관리자 여부", example = "USER")
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_role", nullable = false)
    private AdminRole adminRole = AdminRole.USER;
  
    @Schema(description = "정식 회원가입 여부", example = "true")
    @Column(name = "is_registered", nullable = false)
    private Boolean isRegistered = Boolean.FALSE;
    
    @Schema(description = "생성 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시각", example = "2024-01-01T00:00:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Schema(description = "삭제 시각", example = "2024-01-01T00:00:00")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Schema(description = "사용자 프로필 사진 목록")
    @OneToMany(mappedBy = "user")
    private List<UserPhoto> photos = new ArrayList<>();
    
    @Schema(description = "사용자 시간표 목록")
    @OneToMany(mappedBy = "user")
    private List<UserTimetable> timetables = new ArrayList<>();
    
    @Schema(description = "동아리 멤버십 목록")
    @OneToMany(mappedBy = "user")
    private List<ClubMember> clubMemberships = new ArrayList<>();
    
    @Schema(description = "동아리 이벤트 참가 목록")
    @OneToMany(mappedBy = "user")
    private List<ClubEventParticipant> clubEventParticipations = new ArrayList<>();
    
    @Schema(description = "업로드한 동아리 갤러리 사진 목록")
    @OneToMany(mappedBy = "uploader")
    private List<ClubGalPhoto> uploadedClubGalPhotos = new ArrayList<>();
    
    @Schema(description = "생성한 동아리 이벤트 목록")
    @OneToMany(mappedBy = "creator")
    private List<ClubEvent> createdClubEvents = new ArrayList<>();
    
    @Schema(description = "팀 멤버십 목록")
    @OneToMany(mappedBy = "user")
    private List<TeamMember> teamMemberships = new ArrayList<>();
    
    @Schema(description = "팀 이벤트 참가 목록")
    @OneToMany(mappedBy = "user")
    private List<TeamEventParticipant> teamEventParticipations = new ArrayList<>();
    
    @Schema(description = "생성한 팀 이벤트 목록")
    @OneToMany(mappedBy = "creator")
    private List<TeamEvent> createdTeamEvents = new ArrayList<>();
    
    @Schema(description = "생성한 투표 목록")
    @OneToMany(mappedBy = "creator")
    private List<Poll> createdPolls = new ArrayList<>();
    
    @Schema(description = "제안한 투표 곡 목록")
    @OneToMany(mappedBy = "suggester")
    private List<PollSong> suggestedPollSongs = new ArrayList<>();
    
    @Schema(description = "투표 내역")
    @OneToMany(mappedBy = "user")
    private List<Vote> votes = new ArrayList<>();
    
    @Schema(description = "생성한 홍보 게시글 목록")
    @OneToMany(mappedBy = "creator")
    private List<Promo> createdPromos = new ArrayList<>();
    
    @Schema(description = "업로드한 홍보 사진 목록")
    @OneToMany(mappedBy = "uploader")
    private List<PromoPhoto> uploadedPromoPhotos = new ArrayList<>();
    
    @Schema(description = "홍보 게시글 좋아요 목록")
    @OneToMany(mappedBy = "user")
    private List<PromoLike> promoLikes = new ArrayList<>();
    
    @Schema(description = "홍보 게시글 신고 목록")
    @OneToMany(mappedBy = "reporter")
    private List<PromoReport> promoReports = new ArrayList<>();
    
    @Schema(description = "생성한 홍보 댓글 목록")
    @OneToMany(mappedBy = "creator")
    private List<PromoComment> createdPromoComments = new ArrayList<>();
    
    @Schema(description = "홍보 댓글 좋아요 목록")
    @OneToMany(mappedBy = "user")
    private List<PromoCommentLike> promoCommentLikes = new ArrayList<>();
    
    @Schema(description = "홍보 댓글 신고 목록")
    @OneToMany(mappedBy = "reporter")
    private List<PromoCommentReport> promoCommentReports = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        position = null;
        university = null;

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Schema(description = "사용자 포지션 (악기/보컬)")
    public enum Position {
        @Schema(description = "보컬")
        VOCAL, 
        @Schema(description = "기타")
        GUITAR, 
        @Schema(description = "키보드")
        KEYBOARD, 
        @Schema(description = "베이스")
        BASS, 
        @Schema(description = "드럼")
        DRUM, 
        @Schema(description = "기타")
        OTHER;

        public static Position from(String name) {
            try {
                return Position.valueOf(name);
            } catch (Exception e) {
                return null;
            }
        }
    }
    
    @Schema(description = "관리자 권한")
    public enum AdminRole {
        @Schema(description = "관리자")
        ADMIN, 
        @Schema(description = "일반 사용자")
        USER
    }
} 
