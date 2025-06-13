package com.jandi.band_backend.team.repository;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.team.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    Optional<Team> findByIdAndDeletedAtIsNull(Integer id);
    Page<Team> findAllByClubAndDeletedAtIsNullOrderByCreatedAtDesc(Club club, Pageable pageable);
    List<Team> findAllByClubIdAndDeletedAtIsNull(Integer clubId);

    @Modifying
    @Query(value = "UPDATE team SET creator_user_id = -1 WHERE creator_user_id = :userId", nativeQuery = true)
    int anonymizeByCreatorId(@Param("userId") Integer userId);
}
