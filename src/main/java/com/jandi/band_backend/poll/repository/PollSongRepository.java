package com.jandi.band_backend.poll.repository;

import com.jandi.band_backend.poll.entity.Poll;
import com.jandi.band_backend.poll.entity.PollSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollSongRepository extends JpaRepository<PollSong, Integer> {
    List<PollSong> findAllByPollAndDeletedAtIsNullOrderByCreatedAtDesc(Poll poll);

    @Modifying
    @Query(value = "UPDATE poll_song SET suggester_user_id = -1 WHERE suggester_user_id = :userId", nativeQuery = true)
    int anonymizeBySuggesterId(@Param("userId") Integer userId);
}
