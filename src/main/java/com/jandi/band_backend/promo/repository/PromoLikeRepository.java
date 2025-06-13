package com.jandi.band_backend.promo.repository;

import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.entity.PromoLike;
import com.jandi.band_backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromoLikeRepository extends JpaRepository<PromoLike, Integer> {
    
    Optional<PromoLike> findByPromoAndUser(Promo promo, Users user);
    
    boolean existsByPromoAndUser(Promo promo, Users user);

    @Query("SELECT pl.promo.id FROM PromoLike pl WHERE pl.user.id = :userId")
    List<Integer> findPromoIdsByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query("DELETE FROM PromoLike pl WHERE pl.user.id = :userId")
    int deleteByUserId(@Param("userId") Integer userId);
}
