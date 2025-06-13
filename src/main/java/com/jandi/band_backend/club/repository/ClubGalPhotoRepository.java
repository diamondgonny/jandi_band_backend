package com.jandi.band_backend.club.repository;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubGalPhoto;
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
public interface ClubGalPhotoRepository extends JpaRepository<ClubGalPhoto, Integer> {
    List<ClubGalPhoto> findByClubId(Integer clubId);
    List<ClubGalPhoto> findByClubIdAndDeletedAtIsNull(Integer clubId);
    List<ClubGalPhoto> findByClubIdAndIsPublic(Integer clubId, boolean isPublic);
    List<ClubGalPhoto> findByClubIdAndIsPinned(Integer clubId, boolean isPinned);

    @Query("SELECT p FROM ClubGalPhoto p JOIN FETCH p.uploader WHERE p.club.id = :clubId AND p.isPublic = :isPublic AND p.deletedAt IS NULL")
    Page<ClubGalPhoto> findByClubIdAndIsPublicAndDeletedAtIsNullFetchUploader(@Param("clubId") Integer clubId, @Param("isPublic") boolean isPublic, Pageable pageable);

    @Query("SELECT p FROM ClubGalPhoto p JOIN FETCH p.uploader WHERE p.club.id = :clubId AND p.deletedAt IS NULL")
    Page<ClubGalPhoto> findByClubIdAndDeletedAtIsNullFetchUploader(@Param("clubId") Integer clubId, Pageable pageable);

    Optional<ClubGalPhoto> findByIdAndClubAndDeletedAtIsNull(Integer id, Club club);

    @Modifying
    @Query(value = "UPDATE club_gal_photo SET uploader_user_id = -1 WHERE uploader_user_id = :userId", nativeQuery = true)
    int anonymizeByUserId(@Param("userId") Integer userId);
}