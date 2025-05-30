package com.jandi.band_backend.promo.service;

import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.promo.entity.PromoComment;
import com.jandi.band_backend.promo.entity.PromoCommentLike;
import com.jandi.band_backend.promo.repository.PromoCommentLikeRepository;
import com.jandi.band_backend.promo.repository.PromoCommentRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoCommentLikeService {
    
    private final PromoCommentLikeRepository promoCommentLikeRepository;
    private final PromoCommentRepository promoCommentRepository;
    private final UserRepository userRepository;
    
    /**
     * 공연 홍보 댓글 좋아요 추가/취소 토글
     */
    public boolean togglePromoCommentLike(Integer commentId, Integer userId) {
        PromoComment comment = promoCommentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다."));
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
        
        Optional<PromoCommentLike> existingLike = promoCommentLikeRepository.findByPromoCommentAndUser(comment, user);
        
        if (existingLike.isPresent()) {
            promoCommentLikeRepository.delete(existingLike.get());
            return false;
        } else {
            PromoCommentLike commentLike = new PromoCommentLike();
            commentLike.setPromoComment(comment);
            commentLike.setUser(user);
            promoCommentLikeRepository.save(commentLike);
            return true;
        }
    }
    
    /**
     * 사용자가 특정 댓글에 좋아요를 눌렀는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Integer commentId, Integer userId) {
        PromoComment comment = promoCommentRepository.findByIdAndNotDeleted(commentId).orElse(null);
        if (comment == null) {
            return false;
        }
        
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        return promoCommentLikeRepository.existsByPromoCommentAndUser(comment, user);
    }
    
    /**
     * 특정 댓글의 좋아요 수 조회
     */
    @Transactional(readOnly = true)
    public Integer getLikeCount(Integer commentId) {
        PromoComment comment = promoCommentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다."));
        
        return promoCommentLikeRepository.countByPromoComment(comment);
    }
} 