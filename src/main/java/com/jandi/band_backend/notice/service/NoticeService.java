package com.jandi.band_backend.notice.service;

import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.global.exception.InvalidAccessException;
import com.jandi.band_backend.global.exception.BadRequestException;
import com.jandi.band_backend.notice.dto.NoticeReqDTO;
import com.jandi.band_backend.notice.dto.NoticeDetailRespDTO;
import com.jandi.band_backend.notice.dto.NoticeRespDTO;
import com.jandi.band_backend.notice.entity.Notice;
import com.jandi.band_backend.notice.repository.NoticeRepository;
import com.jandi.band_backend.image.S3Service;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    private static final String S3_DIRNAME = "notice-photo";

    private Users validateAdminPermissionAndGetUser(Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        if (user.getAdminRole() != Users.AdminRole.ADMIN) {
            throw new InvalidAccessException("관리자만 접근할 수 있습니다.");
        }

        return user;
    }

    private void validateDateTimeRange(LocalDateTime startDatetime, LocalDateTime endDatetime) {
        if (!endDatetime.isAfter(startDatetime)) {
            throw new BadRequestException("종료 시각은 시작 시각보다 늦어야 합니다.");
        }
    }

    private String sanitizeTitle(String title) {
        // 로깅용 제목 간소화 (길이 제한 및 민감정보 보호)
        if (title == null) return "[null]";
        return title.length() > 50 ? title.substring(0, 50) + "..." : title;
    }

    public List<NoticeRespDTO> getActiveNotices() {
        List<Notice> activeNotices = noticeRepository.findActiveNotices(LocalDateTime.now());
        return activeNotices.stream()
                .map(NoticeRespDTO::new)
                .collect(Collectors.toList());
    }

    public Page<NoticeRespDTO> getAllNotices(Integer userId, Pageable pageable) {
        validateAdminPermissionAndGetUser(userId);

        Page<Notice> notices = noticeRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
        return notices.map(NoticeRespDTO::new);
    }

    public NoticeDetailRespDTO getNoticeDetail(Integer noticeId, Integer userId) {
        validateAdminPermissionAndGetUser(userId);

        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다."));

        return new NoticeDetailRespDTO(notice);
    }

    @Transactional
    public NoticeDetailRespDTO createNotice(NoticeReqDTO reqDTO, Integer creatorId) {
        Users creator = validateAdminPermissionAndGetUser(creatorId);

        validateDateTimeRange(reqDTO.getStartDatetime(), reqDTO.getEndDatetime());

        Notice notice = new Notice();
        notice.setCreator(creator);
        notice.setTitle(reqDTO.getTitle());
        notice.setContent(reqDTO.getContent());
        notice.setStartDatetime(reqDTO.getStartDatetime());
        notice.setEndDatetime(reqDTO.getEndDatetime());
        notice.setIsPaused(reqDTO.getIsPaused());

        // 이미지 업로드 처리
        String imageUrl = null;
        if (reqDTO.getImage() != null && !reqDTO.getImage().isEmpty()) {
            imageUrl = uploadImage(reqDTO.getImage());
        }
        notice.setImageUrl(imageUrl);

        try {
            Notice savedNotice = noticeRepository.save(notice);
            log.info("공지사항 생성 완료 - ID: {}, 제목: {}", savedNotice.getId(), sanitizeTitle(savedNotice.getTitle()));
            return new NoticeDetailRespDTO(savedNotice);
        } catch (Exception e) {
            // DB 저장 실패 시 업로드된 이미지 삭제 (롤백 처리)
            if (imageUrl != null) {
                deleteImage(imageUrl);
            }
            throw new RuntimeException("DB 저장 실패: " + e.getMessage(), e);
        }
    }

    @Transactional
    public NoticeDetailRespDTO updateNotice(Integer noticeId, NoticeReqDTO reqDTO, Integer userId) {
        validateAdminPermissionAndGetUser(userId);

        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다."));

        validateDateTimeRange(reqDTO.getStartDatetime(), reqDTO.getEndDatetime());

        notice.setTitle(reqDTO.getTitle());
        notice.setContent(reqDTO.getContent());
        notice.setStartDatetime(reqDTO.getStartDatetime());
        notice.setEndDatetime(reqDTO.getEndDatetime());

        // 이미지 교체 처리
        String oldImageUrl = notice.getImageUrl();
        if (reqDTO.getImage() != null && !reqDTO.getImage().isEmpty()) {
            String newImageUrl = uploadImage(reqDTO.getImage());
            notice.setImageUrl(newImageUrl);
        }
        // isPaused는 별도의 토글 API에서 변경 가능

        try {
            Notice updatedNotice = noticeRepository.save(notice);
            // 새 이미지로 교체된 경우에만 기존 이미지 삭제
            if (reqDTO.getImage() != null && !reqDTO.getImage().isEmpty() && oldImageUrl != null) {
                deleteImage(oldImageUrl);
            }
            log.info("공지사항 수정 완료 - ID: {}, 제목: {}", updatedNotice.getId(), sanitizeTitle(updatedNotice.getTitle()));
            return new NoticeDetailRespDTO(updatedNotice);
        } catch (Exception e) {
            throw new RuntimeException("DB 저장 실패: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteNotice(Integer noticeId, Integer userId) {
        validateAdminPermissionAndGetUser(userId);

        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다."));

        String imageUrl = notice.getImageUrl();

        // soft delete 처리
        try {
            notice.setDeletedAt(LocalDateTime.now());
            noticeRepository.save(notice);
        } catch (Exception e) {
            throw new RuntimeException("DB 삭제 실패", e);
        }

        // DB 반영 후 S3 이미지 삭제
        if (imageUrl != null) {
            deleteImage(imageUrl);
        }

        log.info("공지사항 삭제 완료 - ID: {}, 제목: {}", notice.getId(), sanitizeTitle(notice.getTitle()));
    }

    @Transactional
    public NoticeRespDTO toggleNoticeStatus(Integer noticeId, Integer userId) {
        validateAdminPermissionAndGetUser(userId);

        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다."));

        notice.setIsPaused(!notice.getIsPaused());
        Notice updatedNotice = noticeRepository.save(notice);

        log.info("공지사항 상태 변경 완료 - ID: {}, 일시정지: {}", updatedNotice.getId(), updatedNotice.getIsPaused());

        return new NoticeRespDTO(updatedNotice);
    }

    /// S3 이미지 처리 관련
    private String uploadImage(MultipartFile file){
        try {
            return s3Service.uploadImage(file, S3_DIRNAME);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage(), e);
        }
    }

    private void deleteImage(String imageUrl){
        try {
            if (imageUrl != null) {
                s3Service.deleteImage(imageUrl);
            }
        } catch (Exception e) {
            log.warn("기존 이미지 삭제 실패: {}", imageUrl, e);
        }
    }
}
