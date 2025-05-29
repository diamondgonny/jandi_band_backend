package com.jandi.band_backend.global.exception;

// 팀 탈퇴 불허 예외
public class TeamLeaveNotAllowedException extends RuntimeException {
    public TeamLeaveNotAllowedException(String message) {
        super(message);
    }
}
