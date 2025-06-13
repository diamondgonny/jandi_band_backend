package com.jandi.band_backend.promo.repository;

import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.entity.PromoComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoCommentRepository extends JpaRepository<PromoComment, Integer> {
    
    @Query("SELECT pc FROM PromoComment pc WHERE pc.deletedAt IS NULL AND pc.id = :id")
    Optional<PromoComment> findByIdAndNotDeleted(@Param("id") Integer id);
    
    @Query("SELECT pc FROM PromoComment pc WHERE pc.deletedAt IS NULL AND pc.promo = :promo ORDER BY pc.createdAt ASC")
    Page<PromoComment> findByPromoAndNotDeleted(@Param("promo") Promo promo, Pageable pageable);
    
    @Query("SELECT COUNT(pc) FROM PromoComment pc WHERE pc.deletedAt IS NULL AND pc.promo = :promo")
    Integer countByPromoAndNotDeleted(@Param("promo") Promo promo);

    @Modifying
    @Query(value = "UPDATE promo_comment SET creator_user_id = -1 WHERE creator_user_id = :userId", nativeQuery = true)
    int anonymizeByUserId(@Param("userId") Integer userId);
}
