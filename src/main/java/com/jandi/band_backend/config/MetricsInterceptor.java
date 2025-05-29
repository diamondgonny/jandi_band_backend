package com.jandi.band_backend.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HTTP 요청에 대한 메트릭을 수집하는 인터셉터
 * 모든 API 호출의 응답 시간, 상태 코드, 엔드포인트 정보를 수집합니다.
 */
@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private final MeterRegistry meterRegistry;
    private final MetricsConfig metricsConfig;

    @Autowired
    public MetricsInterceptor(MeterRegistry meterRegistry, MetricsConfig metricsConfig) {
        this.meterRegistry = meterRegistry;
        this.metricsConfig = metricsConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 요청 시작 시간을 request attribute에 저장
        request.setAttribute("startTime", System.currentTimeMillis());
        
        // API 호출 카운터 증가
        if (metricsConfig.getApiCallCounter() != null) {
            metricsConfig.getApiCallCounter().increment();
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) {
        
        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime != null) {
            // 응답 시간 계산
            long duration = System.currentTimeMillis() - startTime;
            
            // HTTP 메서드와 URI 정보
            String method = request.getMethod();
            String uri = getUriPattern(request.getRequestURI());
            String status = String.valueOf(response.getStatus());
            
            // 응답 시간 메트릭 기록
            Timer.Sample sample = Timer.start(meterRegistry);
            sample.stop(Timer.builder("jandi.http.requests.duration")
                .description("HTTP request duration")
                .tag("method", method)
                .tag("uri", uri)
                .tag("status", status)
                .register(meterRegistry));
            
            // 상태 코드별 카운터 증가
            Counter.builder("jandi.http.requests.total")
                .description("Total HTTP requests")
                .tag("method", method)
                .tag("uri", uri)
                .tag("status", status)
                .register(meterRegistry)
                .increment();
            
            // 에러 상태 코드 별도 추적
            if (response.getStatus() >= 400) {
                String errorType = response.getStatus() >= 500 ? "5xx" : "4xx";
                Counter.builder("jandi.http.errors.total")
                    .description("Total HTTP errors")
                    .tag("method", method)
                    .tag("uri", uri)
                    .tag("status", errorType)
                    .register(meterRegistry)
                    .increment();
            }
        }
    }
    
    /**
     * URI를 패턴화하여 메트릭의 카디널리티를 줄입니다.
     * 예: /api/users/123 -> /api/users/{id}
     */
    private String getUriPattern(String uri) {
        if (uri == null) return "unknown";
        
        // ID 패턴 치환 (숫자)
        uri = uri.replaceAll("/\\d+", "/{id}");
        
        // UUID 패턴 치환
        uri = uri.replaceAll("/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", "/{uuid}");
        
        // 기타 파라미터 패턴 치환
        uri = uri.replaceAll("/[^/]+\\.(jpg|jpeg|png|gif|css|js)", "/static");
        
        return uri;
    }
} 