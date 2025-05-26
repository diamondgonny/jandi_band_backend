package com.jandi.band_backend.global;

import com.jandi.band_backend.global.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    /// 일반적인 예외 처리
    // 전역적 런타임 에러
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<?>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(ex.getMessage(), "RUNTIME_EXCEPTION"));
    }
    // 기타 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error("서버 내부 오류가 발생했습니다", "INTERNAL_ERROR"));
    }

    /// 미존재 예외 처리
    // 리소스 미존재 (일반적인 경우)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }

    // 사용자 미존재
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(ex.getMessage(), "USER_NOT_FOUND"));
    }

    // 대학 미존재
    @ExceptionHandler(UniversityNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleUniversityNotFound(UniversityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(ex.getMessage(), "UNIVERSITY_NOT_FOUND"));
    }

    // 동아리 미존재
    @ExceptionHandler(ClubNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleClubNotFound(ClubNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(ex.getMessage(), "CLUB_NOT_FOUND"));
    }

    // 팀 미존재
    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleTeamNotFound(TeamNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(ex.getMessage(), "TEAM_NOT_FOUND"));
    }

    // 투표 미존재
    @ExceptionHandler(PollNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handlePollNotFound(PollNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(ex.getMessage(), "POLL_NOT_FOUND"));
    }

    // 투표 노래 미존재
    @ExceptionHandler(PollSongNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handlePollSongNotFound(PollSongNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(ex.getMessage(), "POLL_SONG_NOT_FOUND"));
    }

    // 시간표 미존재
    @ExceptionHandler(TimetableNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleTimetableNotFound(TimetableNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(ex.getMessage(), "TIMETABLE_NOT_FOUND"));
    }

    /// 부적절 접근 처리
    // 행사한 해당 투표 미존재
    @ExceptionHandler(VoteNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleVoteNotFound(VoteNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.error(ex.getMessage(), "VOTE_NOT_FOUND"));
    }

    // 행사한 해당 투표 이미 존재
    @ExceptionHandler(VoteAlreadyExistsException.class)
    public ResponseEntity<CommonResponse<?>> handleVoteAlreadyExists(VoteAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict - 리소스 충돌
                .body(CommonResponse.error(ex.getMessage(), "VOTE_ALREADY_EXISTS"));
    }

    // 동아리 접근 권한 없음
    @ExceptionHandler(UnauthorizedClubAccessException.class)
    public ResponseEntity<CommonResponse<?>> handleUnauthorizedClubAccess(UnauthorizedClubAccessException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(CommonResponse.error(ex.getMessage(), "UNAUTHORIZED_CLUB_ACCESS"));
    }

    // 부적절한 토큰
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<CommonResponse<?>> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.error(ex.getMessage(), "INVALID_TOKEN"));
    }

    // 잘못된 접근
    @ExceptionHandler(InvalidAccessException.class)
    public ResponseEntity<CommonResponse<?>> handleInvalidAccess(InvalidAccessException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(CommonResponse.error(ex.getMessage(), "INVALID_ACCESS"));
    }

    // 잘못된 인자
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(CommonResponse.error(ex.getMessage(), "ILLEGAL_ARGUMENT"));
    }

    /// 카카오 예외 처리
    // 카카오 로그인 토큰 발급 실패
    @ExceptionHandler(FailKakaoLoginException.class)
    public ResponseEntity<CommonResponse<?>> handleFailKakaoLogin(FailKakaoLoginException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.error(ex.getMessage(), "FAIL_KAKAO_LOGIN"));
    }

    // 카카오 유저 정보 조회 실패
    @ExceptionHandler(FailKakaoReadUserException.class)
    public ResponseEntity<CommonResponse<?>> handleFailKakaoReadUser(FailKakaoReadUserException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.error(ex.getMessage(), "FAIL_KAKAO_USER"));
    }
}
