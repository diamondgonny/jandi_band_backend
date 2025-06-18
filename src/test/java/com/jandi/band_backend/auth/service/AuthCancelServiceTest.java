package com.jandi.band_backend.auth.service;

import com.jandi.band_backend.auth.service.kakao.KakaoUserService;
import com.jandi.band_backend.club.entity.ClubMember;
import com.jandi.band_backend.club.repository.ClubEventRepository;
import com.jandi.band_backend.club.repository.ClubGalPhotoRepository;
import com.jandi.band_backend.club.repository.ClubMemberRepository;
import com.jandi.band_backend.global.exception.UserNotFoundException;
import com.jandi.band_backend.poll.repository.PollRepository;
import com.jandi.band_backend.poll.repository.PollSongRepository;
import com.jandi.band_backend.poll.repository.VoteRepository;
import com.jandi.band_backend.promo.repository.*;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import com.jandi.band_backend.team.repository.TeamEventRepository;
import com.jandi.band_backend.team.repository.TeamMemberRepository;
import com.jandi.band_backend.team.repository.TeamRepository;
import com.jandi.band_backend.univ.repository.UniversityRepository;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserPhotoRepository;
import com.jandi.band_backend.user.repository.UserRepository;
import com.jandi.band_backend.user.repository.UserTimetableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 회원탈퇴 테스트")
class AuthCancelServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private UserRepository userRepository;
    @Mock private UserPhotoRepository userPhotoRepository;
    @Mock private UserTimetableRepository userTimetableRepository;
    @Mock private UniversityRepository universityRepository;
    @Mock private ClubMemberRepository clubMemberRepository;
    @Mock private TeamMemberRepository teamMemberRepository;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private KakaoUserService kakaoUserService;

    // 그룹 2
    @Mock private ClubGalPhotoRepository clubGalPhotoRepository;
    @Mock private ClubEventRepository clubEventRepository;
    @Mock private PollRepository pollRepository;
    @Mock private PollSongRepository pollSongRepository;
    @Mock private PromoRepository promoRepository;
    @Mock private PromoPhotoRepository promoPhotoRepository;
    @Mock private PromoCommentRepository promoCommentRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private TeamEventRepository teamEventRepository;
    @Mock private PromoReportRepository promoReportRepository;
    @Mock private PromoCommentReportRepository promoCommentReportRepository;

    // 그룹 3
    @Mock private VoteRepository voteRepository;
    @Mock private PromoLikeRepository promoLikeRepository;
    @Mock private PromoCommentLikeRepository promoCommentLikeRepository;

    private Users testUser;
    private final Integer TEST_USER_ID = 1;
    private final String TEST_KAKAO_OAUTH_ID = "test_kakao_123";

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(TEST_USER_ID);
        testUser.setKakaoOauthId(TEST_KAKAO_OAUTH_ID);
        testUser.setNickname("테스트사용자");
        testUser.setIsRegistered(true);
    }

    @Test
    @DisplayName("1. 정상 케이스 - 일반 회원의 정상 탈퇴")
    void cancel_Success_NormalUser() {
        // Given
        LocalDateTime testStartTime = LocalDateTime.now();

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Collections.emptyList());

        // 그룹 1 모킹
        when(userPhotoRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(1);
        when(userTimetableRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(2);
        when(clubMemberRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(1);
        when(teamMemberRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(1);

        // 그룹 2 모킹
        when(clubGalPhotoRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(3);
        when(clubEventRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(2);
        when(pollRepository.anonymizeByCreatorId(TEST_USER_ID)).thenReturn(1);
        when(pollSongRepository.anonymizeBySuggesterId(TEST_USER_ID)).thenReturn(5);
        when(promoRepository.anonymizeByCreatorId(TEST_USER_ID)).thenReturn(2);
        when(promoPhotoRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(4);
        when(promoCommentRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(8);
        when(teamRepository.anonymizeByCreatorId(TEST_USER_ID)).thenReturn(1);
        when(teamEventRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(3);
        when(promoReportRepository.anonymizeByReporterId(TEST_USER_ID)).thenReturn(0);
        when(promoCommentReportRepository.anonymizeByReporterId(TEST_USER_ID)).thenReturn(1);

        // 그룹 3 모킹
        when(voteRepository.deleteByUserId(TEST_USER_ID)).thenReturn(10);
        when(promoLikeRepository.findPromoIdsByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(1, 2, 3));
        when(promoLikeRepository.deleteByUserId(TEST_USER_ID)).thenReturn(3);
        when(promoCommentLikeRepository.deleteByUserId(TEST_USER_ID)).thenReturn(5);

        // When
        assertDoesNotThrow(() -> authService.cancel(TEST_USER_ID));

        // Then
        verify(userRepository).findById(TEST_USER_ID);
        verify(clubMemberRepository).findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE);

        // 그룹 1 검증
        verify(userPhotoRepository).softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class));
        verify(userTimetableRepository).softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class));
        verify(clubMemberRepository).softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class));
        verify(teamMemberRepository).softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class));

        // 그룹 2 검증
        verify(clubGalPhotoRepository).anonymizeByUserId(TEST_USER_ID);
        verify(clubEventRepository).anonymizeByUserId(TEST_USER_ID);
        verify(pollRepository).anonymizeByCreatorId(TEST_USER_ID);
        verify(pollSongRepository).anonymizeBySuggesterId(TEST_USER_ID);
        verify(promoRepository).anonymizeByCreatorId(TEST_USER_ID);
        verify(promoPhotoRepository).anonymizeByUserId(TEST_USER_ID);
        verify(promoCommentRepository).anonymizeByUserId(TEST_USER_ID);
        verify(teamRepository).anonymizeByCreatorId(TEST_USER_ID);
        verify(teamEventRepository).anonymizeByUserId(TEST_USER_ID);
        verify(promoReportRepository).anonymizeByReporterId(TEST_USER_ID);
        verify(promoCommentReportRepository).anonymizeByReporterId(TEST_USER_ID);

        // 그룹 3 검증
        verify(voteRepository).deleteByUserId(TEST_USER_ID);
        verify(promoLikeRepository).findPromoIdsByUserId(TEST_USER_ID);
        verify(promoRepository, times(3)).decrementLikeCount(anyInt());
        verify(promoLikeRepository).deleteByUserId(TEST_USER_ID);
        verify(promoCommentLikeRepository).deleteByUserId(TEST_USER_ID);

        // 사용자 상태 변경 검증
        assertFalse(testUser.getIsRegistered());
        assertNotNull(testUser.getDeletedAt());
        assertTrue(testUser.getDeletedAt().isAfter(testStartTime) ||
                   testUser.getDeletedAt().isEqual(testStartTime));
        verify(userRepository).save(testUser);

        // 카카오 연결 해제 검증
        verify(kakaoUserService).unlink(TEST_KAKAO_OAUTH_ID);
    }

    @Test
    @DisplayName("2-1. 존재하지 않는 사용자 ID로 탈퇴 시도")
    void cancel_ThrowsException_UserNotFound() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> authService.cancel(TEST_USER_ID));

        verify(userRepository).findById(TEST_USER_ID);
        verify(clubMemberRepository, never()).findClubNamesByUserRole(anyInt(), any());
        verify(kakaoUserService, never()).unlink(anyString());
    }

    @Test
    @DisplayName("2-2. 이미 탈퇴한 사용자가 다시 탈퇴 시도")
    void cancel_Success_AlreadyDeletedUser() {
        // Given
        testUser.setDeletedAt(LocalDateTime.now().minusDays(1));
        testUser.setIsRegistered(false);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Collections.emptyList());

        setupRepositoryMocks();

        // When
        assertDoesNotThrow(() -> authService.cancel(TEST_USER_ID));

        // Then
        verify(userRepository).findById(TEST_USER_ID);
        verify(kakaoUserService).unlink(TEST_KAKAO_OAUTH_ID);
    }

    @Test
    @DisplayName("3-1. 단일 동아리 대표자가 탈퇴 시도")
    void cancel_ThrowsException_SingleClubRepresentative() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Arrays.asList("밴드동아리"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.cancel(TEST_USER_ID));

        assertTrue(exception.getMessage().contains("탈퇴할 수 없습니다"));
        assertTrue(exception.getMessage().contains("밴드동아리"));

        verify(userRepository).findById(TEST_USER_ID);
        verify(clubMemberRepository).findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE);
        verify(kakaoUserService, never()).unlink(anyString());
    }

    @Test
    @DisplayName("3-2. 다중 동아리 대표자가 탈퇴 시도")
    void cancel_ThrowsException_MultipleClubRepresentative() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Arrays.asList("밴드동아리", "음악동아리"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.cancel(TEST_USER_ID));

        assertTrue(exception.getMessage().contains("탈퇴할 수 없습니다"));
        assertTrue(exception.getMessage().contains("밴드동아리, 음악동아리"));

        verify(userRepository).findById(TEST_USER_ID);
        verify(clubMemberRepository).findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE);
        verify(kakaoUserService, never()).unlink(anyString());
    }

    @Test
    @DisplayName("4-1. 그룹 1 소프트 삭제 중 오류 발생")
    void cancel_ThrowsException_Group1SoftDeleteFailure() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Collections.emptyList());

        // 그룹 1 중 하나에서 오류 발생
        when(userPhotoRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class)))
                .thenThrow(new DataAccessException("DB 오류") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> authService.cancel(TEST_USER_ID));

        verify(userRepository).findById(TEST_USER_ID);
        verify(userPhotoRepository).softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class));
        verify(kakaoUserService, never()).unlink(anyString());
    }

    @Test
    @DisplayName("4-2. 그룹 2 익명화 처리 중 오류 발생")
    void cancel_ThrowsException_Group2AnonymizeFailure() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Collections.emptyList());

        // 그룹 1 성공
        setupGroup1Mocks();

        // 그룹 2 중 하나에서 오류 발생
        when(clubGalPhotoRepository.anonymizeByUserId(TEST_USER_ID))
                .thenThrow(new DataAccessException("익명화 오류") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> authService.cancel(TEST_USER_ID));

        verify(userRepository).findById(TEST_USER_ID);
        verify(clubGalPhotoRepository).anonymizeByUserId(TEST_USER_ID);
        verify(kakaoUserService, never()).unlink(anyString());
    }

    @Test
    @DisplayName("4-3. 그룹 3 하드 삭제 중 오류 발생")
    void cancel_ThrowsException_Group3HardDeleteFailure() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Collections.emptyList());

        // 그룹 1, 2 성공
        setupGroup1Mocks();
        setupGroup2Mocks();

        // 그룹 3 중 하나에서 오류 발생
        when(voteRepository.deleteByUserId(TEST_USER_ID))
                .thenThrow(new DataAccessException("하드 삭제 오류") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> authService.cancel(TEST_USER_ID));

        verify(userRepository).findById(TEST_USER_ID);
        verify(voteRepository).deleteByUserId(TEST_USER_ID);
        verify(kakaoUserService, never()).unlink(anyString());
    }

    @Test
    @DisplayName("5-1. 카카오 연결 해제 실패 시 롤백")
    void cancel_ThrowsException_KakaoUnlinkFailure() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Collections.emptyList());

        // 모든 데이터 처리 성공
        setupRepositoryMocks();

        // 카카오 연결 해제에서 오류 발생
        doThrow(new RuntimeException("카카오 API 오류")).when(kakaoUserService).unlink(TEST_KAKAO_OAUTH_ID);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.cancel(TEST_USER_ID));
        assertEquals("카카오 API 오류", exception.getMessage());

        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository).save(testUser);
        verify(kakaoUserService).unlink(TEST_KAKAO_OAUTH_ID);

        // 사용자 상태가 변경되었는지 확인 (카카오 해제 실패해도 DB 변경사항은 처리됨)
        assertFalse(testUser.getIsRegistered());
        assertNotNull(testUser.getDeletedAt());

        // 모든 그룹별 처리가 호출되었는지 확인
        verifyAllRepositoryInteractions();
    }

    @Test
    @DisplayName("5-2. 유효하지 않은 카카오 OAuth ID로 연결 해제 시도")
    void cancel_ThrowsException_InvalidKakaoOAuthId() {
        // Given
        testUser.setKakaoOauthId("invalid_oauth_id");

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Collections.emptyList());

        setupRepositoryMocks();

        // 유효하지 않은 OAuth ID로 인한 카카오 API 오류
        doThrow(new IllegalArgumentException("유효하지 않은 OAuth ID")).when(kakaoUserService).unlink("invalid_oauth_id");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.cancel(TEST_USER_ID));
        assertEquals("유효하지 않은 OAuth ID", exception.getMessage());

        verify(kakaoUserService).unlink("invalid_oauth_id");
    }

    @Test
    @DisplayName("6-1. 데이터가 없는 사용자 탈퇴")
    void cancel_Success_UserWithNoData() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Collections.emptyList());

        // 모든 그룹에서 처리할 데이터가 없음 (0건 처리)
        setupEmptyDataMocks();

        // When
        assertDoesNotThrow(() -> authService.cancel(TEST_USER_ID));

        // Then
        verify(userRepository).findById(TEST_USER_ID);
        verifyAllRepositoryInteractions();

        // 사용자 상태 변경 검증
        assertFalse(testUser.getIsRegistered());
        assertNotNull(testUser.getDeletedAt());
        verify(userRepository).save(testUser);

        // 카카오 연결 해제 검증
        verify(kakaoUserService).unlink(TEST_KAKAO_OAUTH_ID);
    }

    @Test
    @DisplayName("7-1. 프로모션 좋아요 데이터가 많은 사용자 탈퇴")
    void cancel_Success_UserWithManyPromoLikes() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(clubMemberRepository.findClubNamesByUserRole(TEST_USER_ID, ClubMember.MemberRole.REPRESENTATIVE))
                .thenReturn(Collections.emptyList());

        setupGroup1Mocks();
        setupGroup2Mocks();

        // 그룹 3: 많은 프로모션 좋아요 데이터
        List<Integer> manyPromoIds = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        when(voteRepository.deleteByUserId(TEST_USER_ID)).thenReturn(15);
        when(promoLikeRepository.findPromoIdsByUserId(TEST_USER_ID)).thenReturn(manyPromoIds);
        when(promoLikeRepository.deleteByUserId(TEST_USER_ID)).thenReturn(10);
        when(promoCommentLikeRepository.deleteByUserId(TEST_USER_ID)).thenReturn(25);

        // When
        assertDoesNotThrow(() -> authService.cancel(TEST_USER_ID));

        // Then
        verify(promoLikeRepository).findPromoIdsByUserId(TEST_USER_ID);
        verify(promoRepository, times(10)).decrementLikeCount(anyInt());
        verify(promoLikeRepository).deleteByUserId(TEST_USER_ID);

        // 각 프로모션의 좋아요 카운트가 감소되었는지 확인
        for (Integer promoId : manyPromoIds) {
            verify(promoRepository).decrementLikeCount(promoId);
        }
    }

    @Test
    @DisplayName("8-1. Null 사용자 ID로 탈퇴 시도")
    void cancel_ThrowsException_NullUserId() {
        // Given
        Integer nullUserId = null;
        when(userRepository.findById(nullUserId)).thenReturn(Optional.empty());

        // When & Then - 실제로는 UserNotFoundException이 발생함
        assertThrows(UserNotFoundException.class, () -> authService.cancel(nullUserId));

        verify(userRepository).findById(nullUserId);
        verify(clubMemberRepository, never()).findClubNamesByUserRole(anyInt(), any());
        verify(kakaoUserService, never()).unlink(anyString());
    }

    @Test
    @DisplayName("8-2. 유효하지 않은 사용자 ID로 탈퇴 시도 (0 이하)")
    void cancel_ThrowsException_InvalidUserId() {
        // Given
        Integer invalidUserId = 0;
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> authService.cancel(invalidUserId));

        verify(userRepository).findById(invalidUserId);
        verify(clubMemberRepository, never()).findClubNamesByUserRole(anyInt(), any());
        verify(kakaoUserService, never()).unlink(anyString());
    }

    // 헬퍼 메서드들
    private void setupRepositoryMocks() {
        setupGroup1Mocks();
        setupGroup2Mocks();
        setupGroup3Mocks();
    }

    private void setupEmptyDataMocks() {
        // 그룹 1: 0건 처리
        when(userPhotoRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(0);
        when(userTimetableRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(0);
        when(clubMemberRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(0);
        when(teamMemberRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(0);

        // 그룹 2: 0건 처리
        when(clubGalPhotoRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(0);
        when(clubEventRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(0);
        when(pollRepository.anonymizeByCreatorId(TEST_USER_ID)).thenReturn(0);
        when(pollSongRepository.anonymizeBySuggesterId(TEST_USER_ID)).thenReturn(0);
        when(promoRepository.anonymizeByCreatorId(TEST_USER_ID)).thenReturn(0);
        when(promoPhotoRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(0);
        when(promoCommentRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(0);
        when(teamRepository.anonymizeByCreatorId(TEST_USER_ID)).thenReturn(0);
        when(teamEventRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(0);
        when(promoReportRepository.anonymizeByReporterId(TEST_USER_ID)).thenReturn(0);
        when(promoCommentReportRepository.anonymizeByReporterId(TEST_USER_ID)).thenReturn(0);

        // 그룹 3: 0건 처리
        when(voteRepository.deleteByUserId(TEST_USER_ID)).thenReturn(0);
        when(promoLikeRepository.findPromoIdsByUserId(TEST_USER_ID)).thenReturn(Collections.emptyList());
        when(promoLikeRepository.deleteByUserId(TEST_USER_ID)).thenReturn(0);
        when(promoCommentLikeRepository.deleteByUserId(TEST_USER_ID)).thenReturn(0);
    }

    private void setupGroup1Mocks() {
        when(userPhotoRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(1);
        when(userTimetableRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(1);
        when(clubMemberRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(1);
        when(teamMemberRepository.softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class))).thenReturn(1);
    }

    private void setupGroup2Mocks() {
        when(clubGalPhotoRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(1);
        when(clubEventRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(1);
        when(pollRepository.anonymizeByCreatorId(TEST_USER_ID)).thenReturn(1);
        when(pollSongRepository.anonymizeBySuggesterId(TEST_USER_ID)).thenReturn(1);
        when(promoRepository.anonymizeByCreatorId(TEST_USER_ID)).thenReturn(1);
        when(promoPhotoRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(1);
        when(promoCommentRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(1);
        when(teamRepository.anonymizeByCreatorId(TEST_USER_ID)).thenReturn(1);
        when(teamEventRepository.anonymizeByUserId(TEST_USER_ID)).thenReturn(1);
        when(promoReportRepository.anonymizeByReporterId(TEST_USER_ID)).thenReturn(1);
        when(promoCommentReportRepository.anonymizeByReporterId(TEST_USER_ID)).thenReturn(1);
    }

    private void setupGroup3Mocks() {
        when(voteRepository.deleteByUserId(TEST_USER_ID)).thenReturn(1);
        when(promoLikeRepository.findPromoIdsByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(1, 2));
        when(promoLikeRepository.deleteByUserId(TEST_USER_ID)).thenReturn(2);
        when(promoCommentLikeRepository.deleteByUserId(TEST_USER_ID)).thenReturn(1);
    }

    private void verifyAllRepositoryInteractions() {
        // 그룹 1 검증
        verify(userPhotoRepository).softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class));
        verify(userTimetableRepository).softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class));
        verify(clubMemberRepository).softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class));
        verify(teamMemberRepository).softDeleteByUserId(eq(TEST_USER_ID), any(LocalDateTime.class));

        // 그룹 2 검증
        verify(clubGalPhotoRepository).anonymizeByUserId(TEST_USER_ID);
        verify(clubEventRepository).anonymizeByUserId(TEST_USER_ID);
        verify(pollRepository).anonymizeByCreatorId(TEST_USER_ID);
        verify(pollSongRepository).anonymizeBySuggesterId(TEST_USER_ID);
        verify(promoRepository).anonymizeByCreatorId(TEST_USER_ID);
        verify(promoPhotoRepository).anonymizeByUserId(TEST_USER_ID);
        verify(promoCommentRepository).anonymizeByUserId(TEST_USER_ID);
        verify(teamRepository).anonymizeByCreatorId(TEST_USER_ID);
        verify(teamEventRepository).anonymizeByUserId(TEST_USER_ID);
        verify(promoReportRepository).anonymizeByReporterId(TEST_USER_ID);
        verify(promoCommentReportRepository).anonymizeByReporterId(TEST_USER_ID);

        // 그룹 3 검증
        verify(voteRepository).deleteByUserId(TEST_USER_ID);
        verify(promoLikeRepository).findPromoIdsByUserId(TEST_USER_ID);
        verify(promoLikeRepository).deleteByUserId(TEST_USER_ID);
        verify(promoCommentLikeRepository).deleteByUserId(TEST_USER_ID);
    }
}
