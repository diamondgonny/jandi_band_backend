package com.jandi.band_backend.promo.service;

import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.promo.dto.PromoCommentReqDTO;
import com.jandi.band_backend.promo.dto.PromoCommentRespDTO;
import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.entity.PromoComment;
import com.jandi.band_backend.promo.repository.PromoCommentRepository;
import com.jandi.band_backend.promo.repository.PromoRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromoCommentService {

    private final PromoCommentRepository promoCommentRepository;
    private final PromoRepository promoRepository;
    private final UserRepository userRepository;
    private final PromoCommentLikeService promoCommentLikeService;

    // 공연 홍보 댓글 목록 조회
    public Page<PromoCommentRespDTO> getCommentsByPromo(Integer promoId, Pageable pageable) {
        Promo promo = promoRepository.findByIdAndNotDeleted(promoId);
        if (promo == null) {
            throw new ResourceNotFoundException("공연 홍보를 찾을 수 없습니다.");
        }
        
        return promoCommentRepository.findByPromoAndNotDeleted(promo, pageable)
                .map(PromoCommentRespDTO::from);
    }

    // 공연 홍보 댓글 목록 조회 (사용자별 좋아요 상태 포함)
    public Page<PromoCommentRespDTO> getCommentsByPromo(Integer promoId, Integer userId, Pageable pageable) {
        Promo promo = promoRepository.findByIdAndNotDeleted(promoId);
        if (promo == null) {
            throw new ResourceNotFoundException("공연 홍보를 찾을 수 없습니다.");
        }
        
        return promoCommentRepository.findByPromoAndNotDeleted(promo, pageable)
                .map(comment -> {
                    Boolean isLikedByUser = userId != null ? 
                            promoCommentLikeService.isLikedByUser(comment.getId(), userId) : null;
                    return PromoCommentRespDTO.from(comment, isLikedByUser);
                });
    }

    // 공연 홍보 댓글 생성
    @Transactional
    public PromoCommentRespDTO createComment(Integer promoId, PromoCommentReqDTO request, Integer creatorId) {
        Promo promo = promoRepository.findByIdAndNotDeleted(promoId);
        if (promo == null) {
            throw new ResourceNotFoundException("공연 홍보를 찾을 수 없습니다.");
        }
        
        Users creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        PromoComment comment = new PromoComment();
        comment.setPromo(promo);
        comment.setCreator(creator);
        comment.setDescription(request.getDescription());

        PromoComment savedComment = promoCommentRepository.save(comment);
        
        // 공연 홍보의 댓글 수 업데이트
        updatePromoCommentCount(promo);
        
        return PromoCommentRespDTO.from(savedComment);
    }

    // 공연 홍보 댓글 수정
    @Transactional
    public PromoCommentRespDTO updateComment(Integer commentId, PromoCommentReqDTO request, Integer userId) {
        PromoComment comment = promoCommentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다."));

        // 권한 체크
        if (!comment.getCreator().getId().equals(userId)) {
            throw new IllegalStateException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setDescription(request.getDescription());
        
        return PromoCommentRespDTO.from(comment);
    }

    // 공연 홍보 댓글 삭제 (소프트 삭제)
    @Transactional
    public void deleteComment(Integer commentId, Integer userId) {
        PromoComment comment = promoCommentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다."));

        // 권한 체크
        if (!comment.getCreator().getId().equals(userId)) {
            throw new IllegalStateException("댓글을 삭제할 권한이 없습니다.");
        }

        comment.setDeletedAt(LocalDateTime.now());
        
        // 공연 홍보의 댓글 수 업데이트
        updatePromoCommentCount(comment.getPromo());
    }
    
    // 공연 홍보의 댓글 수 업데이트
    private void updatePromoCommentCount(Promo promo) {
        Integer commentCount = promoCommentRepository.countByPromoAndNotDeleted(promo);
        promo.setCommentCount(commentCount);
        promoRepository.save(promo);
    }
} 