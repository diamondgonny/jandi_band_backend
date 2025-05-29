package com.jandi.band_backend.team.repository;

import com.jandi.band_backend.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {
    // 사용자 ID로 팀 멤버 조회
    List<TeamMember> findByUserIdAndTeamDeletedAtIsNullAndDeletedAtIsNullOrderByJoinedAtDesc(Integer userId);
    // 팀 ID로 멤버 조회
    List<TeamMember> findByTeamIdAndDeletedAtIsNull(Integer teamId);
    // 팀 ID로 멤버 수 조회
    Integer countByTeamIdAndDeletedAtIsNull(Integer teamId);
    // 팀 ID와 사용자 ID로 멤버 조회
    Optional<TeamMember> findByTeamIdAndUserIdAndDeletedAtIsNull(Integer teamId, Integer userId);
}
