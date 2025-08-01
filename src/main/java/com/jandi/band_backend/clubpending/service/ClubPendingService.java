package com.jandi.band_backend.clubpending.service;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubMember;
import com.jandi.band_backend.club.repository.ClubMemberRepository;
import com.jandi.band_backend.club.repository.ClubRepository;
import com.jandi.band_backend.clubpending.dto.*;
import com.jandi.band_backend.clubpending.entity.ClubPending;
import com.jandi.band_backend.clubpending.entity.ClubPending.PendingStatus;
import com.jandi.band_backend.clubpending.repository.ClubPendingRepository;
import com.jandi.band_backend.global.exception.*;
import com.jandi.band_backend.global.util.PermissionValidationUtil;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubPendingService {

    private final ClubPendingRepository clubPendingRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;
    private final PermissionValidationUtil permissionValidationUtil;

    @Transactional
    public ClubPendingRespDTO applyToClub(Integer userId, Integer clubId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));

        boolean isMember = clubMemberRepository.findByClubIdAndUserId(club.getId(), userId)
                .filter(member -> member.getDeletedAt() == null)
                .isPresent();

        if (isMember) {
            throw new InvalidAccessException("이미 가입한 동아리입니다.");
        }

        Optional<ClubMember> bannedMember = clubMemberRepository.findByClubIdAndUserId(club.getId(), userId)
                .filter(member -> member.getRole() == ClubMember.MemberRole.BANNED);

        if (bannedMember.isPresent()) {
            throw new BannedMemberJoinAttemptException("강퇴된 사용자는 해당 동아리에 재가입할 수 없습니다.");
        }

        Optional<ClubPending> existingPending = clubPendingRepository.findByClubIdAndUserId(club.getId(), userId);

        if (existingPending.isPresent()) {
            ClubPending pending = existingPending.get();
            if (pending.getStatus() == PendingStatus.PENDING) {
                throw new DuplicateApplicationException("이미 신청한 동아리입니다.");
            }
            // 거부/만료된 경우 재신청 가능하도록 상태 업데이트
            if (pending.getStatus() == PendingStatus.REJECTED || pending.getStatus() == PendingStatus.EXPIRED) {
                pending.reapply();
                return ClubPendingRespDTO.from(clubPendingRepository.save(pending));
            }
        }

        try {
            ClubPending newPending = new ClubPending();
            newPending.setClub(club);
            newPending.setUser(user);

            return ClubPendingRespDTO.from(clubPendingRepository.save(newPending));
        } catch (DataIntegrityViolationException e) {
            // 동시성으로 인한 중복 신청이 발생한 경우
            throw new DuplicateApplicationException("이미 신청한 동아리입니다.");
        }
    }

    public ClubPendingListRespDTO getPendingListByClub(Integer clubId, Integer userId) {
        permissionValidationUtil.validateClubRepresentativeAccess(clubId, userId, "신청 목록 조회 권한이 없습니다.");

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));

        List<ClubPending> pendings = clubPendingRepository.findPendingsByClubId(clubId);

        List<ClubPendingRespDTO> pendingDTOs = pendings.stream()
                .map(ClubPendingRespDTO::from)
                .collect(Collectors.toList());

        return ClubPendingListRespDTO.builder()
                .clubId(club.getId())
                .clubName(club.getName())
                .pendingMembers(pendingDTOs)
                .totalCount(pendingDTOs.size())
                .pendingCount(pendingDTOs.size())
                .build();
    }


    public ClubPendingRespDTO getMyPendingForClub(Integer clubId, Integer userId) {
        ClubPending pending = clubPendingRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow(() -> new PendingNotFoundException("해당 동아리에 대한 신청이 없습니다."));

        return ClubPendingRespDTO.from(pending);
    }

    @Transactional
    public ClubPendingRespDTO processPending(Integer pendingId, Integer userId, ClubPendingProcessReqDTO reqDTO) {
        ClubPending pending = clubPendingRepository.findById(pendingId)
                .orElseThrow(PendingNotFoundException::new);

        validateProcessingAuthority(pending, userId);

        Users processor = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        pending.setStatus(reqDTO.getApprove() ? PendingStatus.APPROVED : PendingStatus.REJECTED);
        pending.setProcessedBy(processor);
        pending.setProcessedAt(LocalDateTime.now());

        ClubPending savedPending = clubPendingRepository.save(pending);

        // 승인시 ClubMember로 등록
        if (reqDTO.getApprove()) {
            createClubMember(pending.getClub(), pending.getUser());
        }

        return ClubPendingRespDTO.from(savedPending);
    }

    @Transactional
    public void cancelPending(Integer pendingId, Integer userId) {
        ClubPending pending = clubPendingRepository.findById(pendingId)
                .orElseThrow(PendingNotFoundException::new);

        if (!pending.getUser().getId().equals(userId)) {
            throw new InvalidAccessException("본인의 신청만 취소할 수 있습니다.");
        }

        if (pending.getStatus() != PendingStatus.PENDING) {
            throw new AlreadyProcessedException("대기중인 신청만 취소할 수 있습니다.");
        }

        clubPendingRepository.delete(pending);
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    @Transactional
    public void expirePendingApplications() {
        LocalDateTime now = LocalDateTime.now();
        int expiredCount = clubPendingRepository.bulkExpirePendingApplications(now);
        
        if (expiredCount > 0) {
            // 로깅을 위해 만료된 건수를 기록할 수 있습니다
            // log.info("만료된 가입 신청 {} 건이 처리되었습니다.", expiredCount);
        }
    }

    private void validateProcessingAuthority(ClubPending pending, Integer userId) {
        // 1. 동아리장 권한 확인
        permissionValidationUtil.validateClubRepresentativeAccess(
            pending.getClub().getId(), userId, "승인 권한이 없습니다."
        );

        // 2. Pending 상태가 PENDING인지 확인
        if (pending.getStatus() != PendingStatus.PENDING) {
            throw new AlreadyProcessedException("이미 처리된 신청입니다.");
        }

        // 3. 만료되지 않았는지 확인
        if (pending.getExpiresAt() != null && pending.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ExpiredApplicationException("만료된 신청입니다.");
        }
    }

    private void createClubMember(Club club, Users user) {
        Optional<ClubMember> existingMember = clubMemberRepository.findByClubIdAndUserId(club.getId(), user.getId());

        if (existingMember.isPresent()) {
            ClubMember clubMember = existingMember.get();
            if (clubMember.getDeletedAt() != null) {
                // 소프트 삭제된 상태라면 재활성화
                clubMember.setDeletedAt(null);
                clubMember.setRole(ClubMember.MemberRole.MEMBER);
                clubMember.setUpdatedAt(LocalDateTime.now());
                clubMemberRepository.save(clubMember);
            }
        } else {
            // 기존 회원이 아니라면 새로 생성
            ClubMember clubMember = new ClubMember();
            clubMember.setClub(club);
            clubMember.setUser(user);
            clubMember.setRole(ClubMember.MemberRole.MEMBER);
            clubMemberRepository.save(clubMember);
        }
    }
}
