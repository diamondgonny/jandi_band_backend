package com.jandi.band_backend.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값인 아래의 최상위 필드는 JSON 응답에 포함하지 않음
@Schema(description = "공통 API 응답 형식")
public class CommonRespDTO<T> {

    @Schema(description = "성공 여부", example = "true")
    private final boolean success;   // 성공 여부
    
    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private final String message;    // 응답 메시지
    
    @Schema(description = "실제 응답 데이터 (성공 시)")
    private final T data;            // 실제 응답 데이터 (성공 시)
    
    @Schema(description = "커스텀 에러 코드 (실패 시)", example = "RESOURCE_NOT_FOUND")
    private final String errorCode;  // 커스텀 에러 코드 (실패 시)

    // 성공 시 생성자 (데이터 포함)
    private CommonRespDTO(String message, T data) {
        this.success = true;
        this.message = message;
        this.data = data;
        this.errorCode = null;
    }

    // 성공 시 생성자 (데이터 미포함, 메시지만)
    private CommonRespDTO(String message) {
        this.success = true;
        this.message = message;
        this.data = null;
        this.errorCode = null;
    }

    // 실패 시 생성자
    private CommonRespDTO(String message, String errorCode) {
        this.success = false;
        this.message = message;
        this.data = null;
        this.errorCode = errorCode;
    }

    // 성공 응답 (데이터 + 커스텀 메시지)
    public static <T> CommonRespDTO<T> success(String message, T data) {
        return new CommonRespDTO<>(message, data);
    }

    // 성공 응답 (데이터 없이 커스텀 메시지만, 예: 생성 성공 후 별도 데이터 반환 없을 때)
    public static <T> CommonRespDTO<T> success(String message) {
        return new CommonRespDTO<>(message);
    }

    // 실패 응답 (커스텀 메시지 + 에러 코드)
    public static <T> CommonRespDTO<T> error(String message, String errorCode) {
        return new CommonRespDTO<>(message, errorCode);
    }
}
