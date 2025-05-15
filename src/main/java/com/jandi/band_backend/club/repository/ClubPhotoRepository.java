package com.jandi.band_backend.club.repository;

import com.jandi.band_backend.club.entity.ClubPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubPhotoRepository extends JpaRepository<ClubPhoto, Long> {
    List<ClubPhoto> findByClubId(Long clubId);
    List<ClubPhoto> findByClubIdAndIsPublic(Long clubId, boolean isPublic);
    List<ClubPhoto> findByClubIdAndIsPinned(Long clubId, boolean isPinned);
}
