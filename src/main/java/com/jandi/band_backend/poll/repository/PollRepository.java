package com.jandi.band_backend.poll.repository;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.poll.entity.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Integer> {
    Page<Poll> findAllByClubAndDeletedAtIsNullOrderByCreatedAtDesc(Club club, Pageable pageable);

    Page<Poll> findAllByClubAndEndDatetimeAfterAndDeletedAtIsNullOrderByEndDatetimeAsc(Club club, LocalDateTime now, Pageable pageable);

    Optional<Poll> findByIdAndDeletedAtIsNull(Integer id);

    @Modifying
    @Query(value = "UPDATE poll SET creator_user_id = -1 WHERE creator_user_id = :userId", nativeQuery = true)
    int anonymizeByCreatorId(@Param("userId") Integer userId);
}
