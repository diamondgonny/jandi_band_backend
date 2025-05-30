package com.jandi.band_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정 클래스
 * 메트릭 수집을 위한 인터셉터를 등록합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private MetricsInterceptor metricsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 메트릭 인터셉터 등록
        registry.addInterceptor(metricsInterceptor)
                .addPathPatterns("/**") // 모든 경로에 적용
                .excludePathPatterns(
                    "/actuator/**",     // Actuator 엔드포인트 제외
                    "/swagger-ui/**",   // Swagger UI 제외
                    "/v3/api-docs/**",  // OpenAPI 문서 제외
                    "/error"            // 에러 페이지 제외
                );
    }
} 