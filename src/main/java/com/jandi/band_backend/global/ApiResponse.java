package com.jandi.band_backend.global;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값인 아래의 최상위 필드는 JSON 응답에 포함하지 않음
public class ApiResponse<T> {

    private final boolean success;   // 성공 여부
    private final String message;    // 응답 메시지
    private final T data;            // 실제 응답 데이터 (성공 시)
    private final String errorCode;  // 커스텀 에러 코드 (실패 시)

    // 성공 시 생성자 (데이터 포함)
    private ApiResponse(String message, T data) {
        this.success = true;
        this.message = message;
        this.data = data;
        this.errorCode = null;
    }

    // 성공 시 생성자 (데이터 미포함, 메시지만)
    private ApiResponse(String message) {
        this.success = true;
        this.message = message;
        this.data = null;
        this.errorCode = null;
    }

    // 실패 시 생성자
    private ApiResponse(String message, String errorCode) {
        this.success = false;
        this.message = message;
        this.data = null;
        this.errorCode = errorCode;
    }

    // 성공 응답 (데이터 + 커스텀 메시지)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    // 성공 응답 (데이터 없이 커스텀 메시지만, 예: 생성 성공 후 별도 데이터 반환 없을 때)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message);
    }

    // 실패 응답 (커스텀 메시지 + 에러 코드)
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(message, errorCode);
    }
}
