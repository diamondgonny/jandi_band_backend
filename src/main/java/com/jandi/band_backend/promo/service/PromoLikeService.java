package com.jandi.band_backend.promo.service;

import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.entity.PromoLike;
import com.jandi.band_backend.promo.repository.PromoLikeRepository;
import com.jandi.band_backend.promo.repository.PromoRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoLikeService {
    
    private final PromoLikeRepository promoLikeRepository;
    private final PromoRepository promoRepository;
    private final UserRepository userRepository;
    
    /**
     * 공연 홍보 좋아요 추가/취소 토글
     */
    public boolean togglePromoLike(Integer promoId, Integer userId) {
        Promo promo = promoRepository.findById(promoId)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("공연 홍보를 찾을 수 없습니다."));
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
        
        Optional<PromoLike> existingLike = promoLikeRepository.findByPromoAndUser(promo, user);
        
        if (existingLike.isPresent()) {
            promoLikeRepository.delete(existingLike.get());
            promo.setLikeCount(promo.getLikeCount() - 1);
            promoRepository.save(promo);
            return false;
        } else {
            PromoLike promoLike = new PromoLike();
            promoLike.setPromo(promo);
            promoLike.setUser(user);
            promoLikeRepository.save(promoLike);
            
            promo.setLikeCount(promo.getLikeCount() + 1);
            promoRepository.save(promo);
            return true;
        }
    }
    
    /**
     * 사용자가 특정 공연 홍보에 좋아요를 눌렀는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Integer promoId, Integer userId) {
        Promo promo = promoRepository.findById(promoId)
                .filter(p -> p.getDeletedAt() == null)
                .orElse(null);
        
        if (promo == null) {
            return false;
        }
        
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        return promoLikeRepository.existsByPromoAndUser(promo, user);
    }

    /**
     * 특정 공연 홍보의 좋아요 수 조회
     */
    @Transactional(readOnly = true)
    public Integer getLikeCount(Integer promoId) {
        Promo promo = promoRepository.findById(promoId)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("공연 홍보를 찾을 수 없습니다."));
        
        return promo.getLikeCount();
    }
} 