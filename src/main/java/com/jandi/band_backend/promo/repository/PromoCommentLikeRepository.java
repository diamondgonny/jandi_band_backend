package com.jandi.band_backend.promo.repository;

import com.jandi.band_backend.promo.entity.PromoComment;
import com.jandi.band_backend.promo.entity.PromoCommentLike;
import com.jandi.band_backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoCommentLikeRepository extends JpaRepository<PromoCommentLike, Integer> {
    
    @Query("SELECT pcl FROM PromoCommentLike pcl WHERE pcl.promoComment = :promoComment AND pcl.user = :user")
    Optional<PromoCommentLike> findByPromoCommentAndUser(@Param("promoComment") PromoComment promoComment, @Param("user") Users user);
    
    @Query("SELECT COUNT(pcl) FROM PromoCommentLike pcl WHERE pcl.promoComment = :promoComment")
    Integer countByPromoComment(@Param("promoComment") PromoComment promoComment);
    
    boolean existsByPromoCommentAndUser(PromoComment promoComment, Users user);

    @Modifying
    @Query("DELETE FROM PromoCommentLike pcl WHERE pcl.user.id = :userId")
    int deleteByUserId(@Param("userId") Integer userId);
}
