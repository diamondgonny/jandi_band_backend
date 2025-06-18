package com.jandi.band_backend.poll.service;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.global.exception.ClubNotFoundException;
import com.jandi.band_backend.global.util.EntityValidationUtil;
import com.jandi.band_backend.global.util.UserValidationUtil;
import com.jandi.band_backend.poll.dto.PollReqDTO;
import com.jandi.band_backend.poll.dto.PollRespDTO;
import com.jandi.band_backend.poll.entity.Poll;
import com.jandi.band_backend.poll.repository.PollRepository;
import com.jandi.band_backend.poll.repository.PollSongRepository;
import com.jandi.band_backend.poll.repository.VoteRepository;
import com.jandi.band_backend.user.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PollService 투표 생성/관리 테스트")
class PollCreationServiceTest {

    @InjectMocks
    private PollService pollService;

    @Mock
    private PollRepository pollRepository;

    @Mock
    private PollSongRepository pollSongRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private EntityValidationUtil entityValidationUtil;

    @Mock
    private UserValidationUtil userValidationUtil;

    private Club testClub;
    private Users testUser;
    private PollReqDTO pollReqDTO;
    private Poll testPoll;

    @BeforeEach
    void setUp() {
        testClub = new Club();
        testClub.setId(1);
        testClub.setName("테스트 동아리");

        testUser = new Users();
        testUser.setId(1);
        testUser.setNickname("테스트사용자");

        pollReqDTO = PollReqDTO.builder()
                .title("5월 정기공연 곡 선정")
                .clubId(1)
                .endDatetime(LocalDateTime.now().plusDays(30))
                .build();

        testPoll = new Poll();
        testPoll.setId(1);
        testPoll.setTitle(pollReqDTO.getTitle());
        testPoll.setClub(testClub);
        testPoll.setCreator(testUser);
        testPoll.setStartDatetime(LocalDateTime.now());
        testPoll.setEndDatetime(pollReqDTO.getEndDatetime());
    }

    @Test
    @DisplayName("1. 정상 케이스 - 투표 생성 성공")
    void createPoll_Success() {
        // Given
        when(entityValidationUtil.validateClubExists(1)).thenReturn(testClub);
        when(userValidationUtil.getUserById(1)).thenReturn(testUser);
        when(pollRepository.save(any(Poll.class))).thenReturn(testPoll);

        // When
        PollRespDTO result = pollService.createPoll(pollReqDTO, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("5월 정기공연 곡 선정", result.getTitle());
        assertEquals(1, result.getClubId());
        assertEquals("테스트 동아리", result.getClubName());
        assertEquals(1, result.getCreatorId());
        assertEquals("테스트사용자", result.getCreatorName());

        verify(entityValidationUtil).validateClubExists(1);
        verify(userValidationUtil).getUserById(1);
        verify(pollRepository).save(any(Poll.class));
    }

    @Test
    @DisplayName("2. 존재하지 않는 클럽ID로 투표 생성")
    void createPoll_ThrowsException_ClubNotFound() {
        // Given
        when(entityValidationUtil.validateClubExists(999))
                .thenThrow(new ClubNotFoundException("클럽을 찾을 수 없습니다."));

        PollReqDTO invalidRequest = PollReqDTO.builder()
                .title("테스트 투표")
                .clubId(999)
                .endDatetime(LocalDateTime.now().plusDays(30))
                .build();

        // When & Then
        assertThrows(ClubNotFoundException.class,
                () -> pollService.createPoll(invalidRequest, 1));

        verify(entityValidationUtil).validateClubExists(999);
        verify(userValidationUtil, never()).getUserById(any());
        verify(pollRepository, never()).save(any());
    }

    @Test
    @DisplayName("3. null 클럽ID로 투표 생성")
    void createPoll_ThrowsException_NullClubId() {
        // Given
        PollReqDTO nullClubRequest = PollReqDTO.builder()
                .title("테스트 투표")
                .clubId(null)
                .endDatetime(LocalDateTime.now().plusDays(30))
                .build();

        when(entityValidationUtil.validateClubExists(null))
                .thenThrow(new IllegalArgumentException("클럽 ID는 필수입니다."));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> pollService.createPoll(nullClubRequest, 1));

        verify(entityValidationUtil).validateClubExists(null);
        verify(userValidationUtil, never()).getUserById(any());
        verify(pollRepository, never()).save(any());
    }

    @Test
    @DisplayName("4. null 또는 빈 제목으로 투표 생성")
    void createPoll_WithNullOrEmptyTitle() {
        // Given
        when(entityValidationUtil.validateClubExists(1)).thenReturn(testClub);
        when(userValidationUtil.getUserById(1)).thenReturn(testUser);

        // null 제목
        PollReqDTO nullTitleRequest = PollReqDTO.builder()
                .title(null)
                .clubId(1)
                .endDatetime(LocalDateTime.now().plusDays(30))
                .build();

        Poll nullTitlePoll = new Poll();
        nullTitlePoll.setId(1);
        nullTitlePoll.setTitle(null);
        nullTitlePoll.setClub(testClub);
        nullTitlePoll.setCreator(testUser);

        when(pollRepository.save(any(Poll.class))).thenReturn(nullTitlePoll);

        // When
        PollRespDTO result = pollService.createPoll(nullTitleRequest, 1);

        // Then
        assertNotNull(result);
        assertNull(result.getTitle());

        verify(pollRepository).save(any(Poll.class));
    }

    @Test
    @DisplayName("5. null 마감시간으로 투표 생성")
    void createPoll_WithNullEndDateTime() {
        // Given
        when(entityValidationUtil.validateClubExists(1)).thenReturn(testClub);
        when(userValidationUtil.getUserById(1)).thenReturn(testUser);

        PollReqDTO nullEndDateRequest = PollReqDTO.builder()
                .title("테스트 투표")
                .clubId(1)
                .endDatetime(null)
                .build();

        Poll nullEndDatePoll = new Poll();
        nullEndDatePoll.setId(1);
        nullEndDatePoll.setTitle("테스트 투표");
        nullEndDatePoll.setClub(testClub);
        nullEndDatePoll.setCreator(testUser);
        nullEndDatePoll.setEndDatetime(null);

        when(pollRepository.save(any(Poll.class))).thenReturn(nullEndDatePoll);

        // When
        PollRespDTO result = pollService.createPoll(nullEndDateRequest, 1);

        // Then
        assertNotNull(result);
        assertNull(result.getEndDatetime());

        verify(pollRepository).save(any(Poll.class));
    }

    @Test
    @DisplayName("6. null 사용자ID로 투표 생성")
    void createPoll_ThrowsException_NullUserId() {
        // Given
        when(entityValidationUtil.validateClubExists(1)).thenReturn(testClub);
        when(userValidationUtil.getUserById(null))
                .thenThrow(new IllegalArgumentException("사용자 ID는 필수입니다."));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> pollService.createPoll(pollReqDTO, null));

        verify(entityValidationUtil).validateClubExists(1);
        verify(userValidationUtil).getUserById(null);
        verify(pollRepository, never()).save(any());
    }

    @Test
    @DisplayName("7. 존재하지 않는 사용자로 투표 생성")
    void createPoll_ThrowsException_UserNotFound() {
        // Given
        when(entityValidationUtil.validateClubExists(1)).thenReturn(testClub);
        when(userValidationUtil.getUserById(999))
                .thenThrow(new RuntimeException("사용자를 찾을 수 없습니다."));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> pollService.createPoll(pollReqDTO, 999));

        verify(entityValidationUtil).validateClubExists(1);
        verify(userValidationUtil).getUserById(999);
        verify(pollRepository, never()).save(any());
    }

    @Test
    @DisplayName("8. 클럽 정보가 null인 상태에서 DTO 변환")
    void createPoll_WithNullClubInfo() {
        // Given
        when(entityValidationUtil.validateClubExists(1)).thenReturn(testClub);
        when(userValidationUtil.getUserById(1)).thenReturn(testUser);

        Poll pollWithNullClub = new Poll();
        pollWithNullClub.setId(1);
        pollWithNullClub.setTitle("테스트 투표");
        pollWithNullClub.setClub(null);  // null 클럽
        pollWithNullClub.setCreator(testUser);

        when(pollRepository.save(any(Poll.class))).thenReturn(pollWithNullClub);

        // When
        PollRespDTO result = pollService.createPoll(pollReqDTO, 1);

        // Then
        assertNotNull(result);
        assertNull(result.getClubId());
        assertNull(result.getClubName());
        assertEquals(1, result.getCreatorId());
        assertEquals("테스트사용자", result.getCreatorName());

        verify(pollRepository).save(any(Poll.class));
    }

    @Test
    @DisplayName("9. 사용자 정보가 null인 상태에서 DTO 변환")
    void createPoll_WithNullUserInfo() {
        // Given
        when(entityValidationUtil.validateClubExists(1)).thenReturn(testClub);
        when(userValidationUtil.getUserById(1)).thenReturn(testUser);

        Poll pollWithNullCreator = new Poll();
        pollWithNullCreator.setId(1);
        pollWithNullCreator.setTitle("테스트 투표");
        pollWithNullCreator.setClub(testClub);
        pollWithNullCreator.setCreator(null);  // null 생성자

        when(pollRepository.save(any(Poll.class))).thenReturn(pollWithNullCreator);

        // When
        PollRespDTO result = pollService.createPoll(pollReqDTO, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getClubId());
        assertEquals("테스트 동아리", result.getClubName());
        assertNull(result.getCreatorId());
        assertNull(result.getCreatorName());

        verify(pollRepository).save(any(Poll.class));
    }

    @Test
    @DisplayName("10. Repository 저장 중 예외 발생")
    void createPoll_ThrowsException_RepositorySaveFailure() {
        // Given
        when(entityValidationUtil.validateClubExists(1)).thenReturn(testClub);
        when(userValidationUtil.getUserById(1)).thenReturn(testUser);
        when(pollRepository.save(any(Poll.class)))
                .thenThrow(new RuntimeException("데이터베이스 저장 오류"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> pollService.createPoll(pollReqDTO, 1));

        verify(entityValidationUtil).validateClubExists(1);
        verify(userValidationUtil).getUserById(1);
        verify(pollRepository).save(any(Poll.class));
    }
}
