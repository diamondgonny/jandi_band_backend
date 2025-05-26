package com.jandi.band_backend.global.exception;

// 잘못된 요청 데이터
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
