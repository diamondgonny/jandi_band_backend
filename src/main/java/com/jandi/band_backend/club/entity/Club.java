package com.jandi.band_backend.club.entity;

import com.jandi.band_backend.poll.entity.Poll;
import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.team.entity.Team;
import com.jandi.band_backend.univ.entity.University;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "동아리 엔티티")
@Entity
@Table(name = "club")
@Getter
@Setter
@NoArgsConstructor
public class Club {

    @Schema(description = "동아리 고유 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Integer id;

    @Schema(description = "동아리 이름", example = "서울대 락밴드")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Schema(description = "공식 단톡방 링크", example = "https://open.kakao.com/o/sB1x2y3z")
    @Column(name = "chatroom_url", length = 255)
    private String chatroomUrl;

    @Schema(description = "동아리 소개 및 설명", example = "음악을 사랑하는 사람들이 모인 동아리입니다.")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Schema(description = "동아리 인스타그램 ID", example = "snu_rockband")
    @Column(name = "instagram_id", length = 50)
    private String instagramId;

    @Schema(description = "해당 동아리가 소속된 대학 (null이면 연합 동아리)")
    @ManyToOne
    @JoinColumn(name = "university_id", nullable = true)
    private University university;

    @Schema(description = "생성 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2024-01-01T00:00:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Schema(description = "삭제 시각", example = "2024-01-01T00:00:00")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Schema(description = "동아리 멤버 목록")
    @OneToMany(mappedBy = "club")
    private List<ClubMember> clubMembers = new ArrayList<>();

    @Schema(description = "동아리 이벤트 목록")
    @OneToMany(mappedBy = "club")
    private List<ClubEvent> clubEvents = new ArrayList<>();

    @Schema(description = "동아리 대표 사진 목록")
    @OneToMany(mappedBy = "club")
    private List<ClubPhoto> clubPhotos = new ArrayList<>();

    @Schema(description = "동아리 갤러리 사진 목록")
    @OneToMany(mappedBy = "club")
    private List<ClubGalPhoto> clubGalPhotos = new ArrayList<>();

    @Schema(description = "동아리 소속 팀 목록")
    @OneToMany(mappedBy = "club")
    private List<Team> teams = new ArrayList<>();

    @Schema(description = "동아리 투표 목록")
    @OneToMany(mappedBy = "club")
    private List<Poll> polls = new ArrayList<>();

    @Schema(description = "동아리 홍보글 목록")
    @OneToMany(mappedBy = "club")
    private List<Promo> promos = new ArrayList<>();

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
