package com.jandi.band_backend.config;

import io.micrometer.core.instrument.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 보안 관련 메트릭 설정 클래스
 * 보안 이벤트, 인증/인가 관련 메트릭을 정의합니다.
 */
@Component
public class SecurityMetricsConfig {

    private final MeterRegistry meterRegistry;
    
    // 보안 관련 카운터들
    private Counter loginSuccessCounter;
    private Counter loginFailureCounter;
    private Counter jwtTokenCreationCounter;
    private Counter jwtTokenValidationCounter;
    private Counter unauthorizedAccessCounter;
    private Counter forbiddenAccessCounter;
    
    // 보안 관련 타이머들
    private Timer authenticationTimer;
    private Timer authorizationTimer;
    
    // 보안 관련 게이지들
    private final AtomicInteger suspiciousActivityCount = new AtomicInteger(0);
    private final AtomicInteger blockedIpCount = new AtomicInteger(0);

    @Autowired
    public SecurityMetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initializeSecurityMetrics() {
        // 1. 인증 관련 메트릭
        loginSuccessCounter = Counter.builder("jandi.security.login.success.total")
            .description("Total successful login attempts")
            .tag("provider", "kakao")
            .register(meterRegistry);

        loginFailureCounter = Counter.builder("jandi.security.login.failure.total")
            .description("Total failed login attempts")
            .tag("reason", "invalid_token")
            .register(meterRegistry);

        authenticationTimer = Timer.builder("jandi.security.authentication.duration")
            .description("Time taken for authentication process")
            .register(meterRegistry);

        // 2. JWT 토큰 관련 메트릭
        jwtTokenCreationCounter = Counter.builder("jandi.security.jwt.creation.total")
            .description("Total JWT tokens created")
            .tag("type", "access")
            .register(meterRegistry);

        Counter.builder("jandi.security.jwt.creation.total")
            .description("Total JWT tokens created")
            .tag("type", "refresh")
            .register(meterRegistry);

        jwtTokenValidationCounter = Counter.builder("jandi.security.jwt.validation.total")
            .description("Total JWT token validations")
            .tag("result", "valid")
            .register(meterRegistry);

        Counter.builder("jandi.security.jwt.validation.total")
            .description("Total JWT token validations")
            .tag("result", "invalid")
            .register(meterRegistry);

        Counter.builder("jandi.security.jwt.validation.total")
            .description("Total JWT token validations")
            .tag("result", "expired")
            .register(meterRegistry);

        // 3. 권한 관련 메트릭
        unauthorizedAccessCounter = Counter.builder("jandi.security.unauthorized.total")
            .description("Total unauthorized access attempts")
            .register(meterRegistry);

        forbiddenAccessCounter = Counter.builder("jandi.security.forbidden.total")
            .description("Total forbidden access attempts")
            .register(meterRegistry);

        authorizationTimer = Timer.builder("jandi.security.authorization.duration")
            .description("Time taken for authorization process")
            .register(meterRegistry);

        // 4. 보안 위협 관련 메트릭
        Gauge.builder("jandi.security.suspicious.activity.count", suspiciousActivityCount, AtomicInteger::get)
            .description("Number of suspicious activities detected")
            .register(meterRegistry);

        Counter.builder("jandi.security.attack.attempts.total")
            .description("Total attack attempts")
            .tag("type", "sql_injection")
            .register(meterRegistry);

        Counter.builder("jandi.security.attack.attempts.total")
            .description("Total attack attempts")
            .tag("type", "xss")
            .register(meterRegistry);

        Counter.builder("jandi.security.attack.attempts.total")
            .description("Total attack attempts")
            .tag("type", "csrf")
            .register(meterRegistry);

        // 5. IP 차단 관련 메트릭
        Gauge.builder("jandi.security.blocked.ip.count", blockedIpCount, AtomicInteger::get)
            .description("Number of blocked IP addresses")
            .register(meterRegistry);

        Counter.builder("jandi.security.rate.limit.exceeded.total")
            .description("Total rate limit exceeded events")
            .register(meterRegistry);

        // 6. 세션 관련 메트릭
        Counter.builder("jandi.security.session.created.total")
            .description("Total sessions created")
            .register(meterRegistry);

        Counter.builder("jandi.security.session.expired.total")
            .description("Total sessions expired")
            .register(meterRegistry);

        Counter.builder("jandi.security.session.invalidated.total")
            .description("Total sessions invalidated")
            .register(meterRegistry);
    }

    // Getter 메서드들
    public Counter getLoginSuccessCounter() { return loginSuccessCounter; }
    public Counter getLoginFailureCounter() { return loginFailureCounter; }
    public Counter getJwtTokenCreationCounter() { return jwtTokenCreationCounter; }
    public Counter getJwtTokenValidationCounter() { return jwtTokenValidationCounter; }
    public Counter getUnauthorizedAccessCounter() { return unauthorizedAccessCounter; }
    public Counter getForbiddenAccessCounter() { return forbiddenAccessCounter; }
    
    public Timer getAuthenticationTimer() { return authenticationTimer; }
    public Timer getAuthorizationTimer() { return authorizationTimer; }
    
    public AtomicInteger getSuspiciousActivityCount() { return suspiciousActivityCount; }
    public AtomicInteger getBlockedIpCount() { return blockedIpCount; }
} 