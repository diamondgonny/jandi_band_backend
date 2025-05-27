package com.jandi.band_backend.promo.repository;

import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.entity.PromoLike;
import com.jandi.band_backend.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoLikeRepository extends JpaRepository<PromoLike, Integer> {
    
    @Query("SELECT pl FROM PromoLike pl WHERE pl.promo = :promo AND pl.user = :user")
    Optional<PromoLike> findByPromoAndUser(@Param("promo") Promo promo, @Param("user") Users user);
    
    @Query("SELECT COUNT(pl) FROM PromoLike pl WHERE pl.promo = :promo")
    Integer countByPromo(@Param("promo") Promo promo);
    
    boolean existsByPromoAndUser(Promo promo, Users user);
} 