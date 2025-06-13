package com.jandi.band_backend.global.exception;

public class BannedMemberJoinAttemptException extends RuntimeException {
    public BannedMemberJoinAttemptException(String message) {
        super(message);
    }
}
