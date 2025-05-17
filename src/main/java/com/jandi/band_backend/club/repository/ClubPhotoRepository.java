package com.jandi.band_backend.club.repository;

import com.jandi.band_backend.club.entity.ClubGalPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubPhotoRepository extends JpaRepository<ClubGalPhoto, Long> {
    List<ClubGalPhoto> findByClubId(Long clubId);
    List<ClubGalPhoto> findByClubIdAndIsPublic(Long clubId, boolean isPublic);
    List<ClubGalPhoto> findByClubIdAndIsPinned(Long clubId, boolean isPinned);
}
