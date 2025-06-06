package com.jandi.band_backend.global.exception;

public class TimetableNotFoundException extends RuntimeException {
    public TimetableNotFoundException() {
        super("존재하지 않는 시간표입니다.");
    }

    public TimetableNotFoundException(String message) {
        super(message);
    }
}
