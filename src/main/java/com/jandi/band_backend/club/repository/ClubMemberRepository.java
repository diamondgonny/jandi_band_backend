package com.jandi.band_backend.club.repository;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubMember;
import com.jandi.band_backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, Integer> {
    // 동아리 ID로 멤버 조회
    List<ClubMember> findByClubIdAndDeletedAtIsNull(Integer clubId);
    // 동아리 ID와 사용자 ID로 멤버 조회
    Optional<ClubMember> findByClubIdAndUserIdAndDeletedAtIsNull(Integer clubId, Integer userId);
    // 동아리 ID로 멤버 수 조회
    Integer countByClubIdAndDeletedAtIsNull(Integer clubId);
    // 동아리와 사용자로 멤버 존재 여부 확인
    boolean existsByClubAndUserAndDeletedAtIsNull(Club club, Users user);
    // 사용자 ID로 동아리 멤버 조회 (동아리와 멤버 모두 삭제되지 않은 것만)
    List<ClubMember> findByUserIdAndClubDeletedAtIsNullAndDeletedAtIsNullOrderByJoinedAtDesc(Integer userId);

    @Query("""
    SELECT cm.club.name
    FROM ClubMember cm
    WHERE cm.user.id = :userId
      AND cm.role = :role
      AND cm.deletedAt IS NULL
      AND cm.club.deletedAt IS NULL
""")
    List<String> findClubNamesByUserRole(@Param("userId") Integer userId, @Param("role") ClubMember.MemberRole role);
}
