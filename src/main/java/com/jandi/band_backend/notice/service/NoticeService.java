package com.jandi.band_backend.notice.service;

import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.global.exception.InvalidAccessException;
import com.jandi.band_backend.global.exception.BadRequestException;
import com.jandi.band_backend.notice.dto.NoticeReqDTO;
import com.jandi.band_backend.notice.dto.NoticeDetailRespDTO;
import com.jandi.band_backend.notice.dto.NoticeRespDTO;
import com.jandi.band_backend.notice.entity.Notice;
import com.jandi.band_backend.notice.repository.NoticeRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Notice savedNotice = noticeRepository.save(notice);
        log.info("공지사항 생성 완료 - ID: {}, 제목: {}", savedNotice.getId(), sanitizeTitle(savedNotice.getTitle()));

        return new NoticeDetailRespDTO(savedNotice);
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
        // isPaused는 별도의 토글 API에서 변경 가능

        Notice updatedNotice = noticeRepository.save(notice);
        log.info("공지사항 수정 완료 - ID: {}, 제목: {}", updatedNotice.getId(), sanitizeTitle(updatedNotice.getTitle()));

        return new NoticeDetailRespDTO(updatedNotice);
    }

    @Transactional
    public void deleteNotice(Integer noticeId, Integer userId) {
        validateAdminPermissionAndGetUser(userId);

        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항을 찾을 수 없습니다."));

        notice.setDeletedAt(LocalDateTime.now());
        noticeRepository.save(notice);

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
}
