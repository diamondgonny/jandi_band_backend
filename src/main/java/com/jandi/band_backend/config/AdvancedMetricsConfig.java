package com.jandi.band_backend.config;

import io.micrometer.core.instrument.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 고급 비즈니스 메트릭 설정 클래스
 * 실제 비즈니스 로직과 연관된 상세한 메트릭을 정의합니다.
 */
@Component
public class AdvancedMetricsConfig {

    private final MeterRegistry meterRegistry;
    
    // 비즈니스 메트릭 카운터들
    private Counter userRegistrationCounter;
    private Counter clubCreationCounter;
    private Counter teamJoinCounter;
    private Counter promoViewCounter;
    private Counter commentCreationCounter;
    
    // 비즈니스 메트릭 타이머들
    private Timer databaseQueryTimer;
    private Timer fileUploadTimer;
    private Timer emailSendTimer;
    
    // 비즈니스 메트릭 게이지들
    private final AtomicInteger activeClubsCount = new AtomicInteger(0);
    private final AtomicInteger activeTeamsCount = new AtomicInteger(0);
    private final AtomicInteger onlineUsersCount = new AtomicInteger(0);

    @Autowired
    public AdvancedMetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initializeMetrics() {
        // 1. 사용자 관련 메트릭
        userRegistrationCounter = Counter.builder("jandi.user.registrations.total")
            .description("Total number of user registrations")
            .tag("type", "kakao")
            .register(meterRegistry);

        // 2. 동아리 관련 메트릭
        clubCreationCounter = Counter.builder("jandi.club.creations.total")
            .description("Total number of club creations")
            .register(meterRegistry);

        Gauge.builder("jandi.clubs.active.count", activeClubsCount, AtomicInteger::get)
            .description("Number of active clubs")
            .register(meterRegistry);

        // 3. 팀 관련 메트릭
        teamJoinCounter = Counter.builder("jandi.team.joins.total")
            .description("Total number of team joins")
            .register(meterRegistry);

        Gauge.builder("jandi.teams.active.count", activeTeamsCount, AtomicInteger::get)
            .description("Number of active teams")
            .register(meterRegistry);

        // 4. 홍보 관련 메트릭
        promoViewCounter = Counter.builder("jandi.promo.views.total")
            .description("Total number of promotion views")
            .register(meterRegistry);

        // 5. 댓글 관련 메트릭
        commentCreationCounter = Counter.builder("jandi.comments.creations.total")
            .description("Total number of comment creations")
            .register(meterRegistry);

        // 6. 성능 관련 메트릭
        databaseQueryTimer = Timer.builder("jandi.database.query.duration")
            .description("Database query execution time")
            .register(meterRegistry);

        fileUploadTimer = Timer.builder("jandi.file.upload.duration")
            .description("File upload processing time")
            .register(meterRegistry);

        emailSendTimer = Timer.builder("jandi.email.send.duration")
            .description("Email sending time")
            .register(meterRegistry);

        // 7. 실시간 상태 메트릭
        Gauge.builder("jandi.users.online.count", onlineUsersCount, AtomicInteger::get)
            .description("Number of online users")
            .register(meterRegistry);

        // 8. 에러 분류 메트릭
        Counter.builder("jandi.errors.business.total")
            .description("Total business logic errors")
            .tag("error_type", "validation")
            .register(meterRegistry);

        Counter.builder("jandi.errors.business.total")
            .description("Total business logic errors")
            .tag("error_type", "permission")
            .register(meterRegistry);

        Counter.builder("jandi.errors.business.total")
            .description("Total business logic errors")
            .tag("error_type", "not_found")
            .register(meterRegistry);

        // 9. 보안 관련 메트릭
        Counter.builder("jandi.security.login.attempts.total")
            .description("Total login attempts")
            .tag("result", "success")
            .register(meterRegistry);

        Counter.builder("jandi.security.login.attempts.total")
            .description("Total login attempts")
            .tag("result", "failure")
            .register(meterRegistry);

        // 10. API 성능 분석 메트릭
        Timer.builder("jandi.api.response.time")
            .description("API response time by endpoint")
            .tag("endpoint", "user_registration")
            .register(meterRegistry);

        Timer.builder("jandi.api.response.time")
            .description("API response time by endpoint")
            .tag("endpoint", "club_creation")
            .register(meterRegistry);
    }

    // Getter 메서드들 (다른 클래스에서 메트릭을 업데이트할 때 사용)
    public Counter getUserRegistrationCounter() { return userRegistrationCounter; }
    public Counter getClubCreationCounter() { return clubCreationCounter; }
    public Counter getTeamJoinCounter() { return teamJoinCounter; }
    public Counter getPromoViewCounter() { return promoViewCounter; }
    public Counter getCommentCreationCounter() { return commentCreationCounter; }
    
    public Timer getDatabaseQueryTimer() { return databaseQueryTimer; }
    public Timer getFileUploadTimer() { return fileUploadTimer; }
    public Timer getEmailSendTimer() { return emailSendTimer; }
    
    public AtomicInteger getActiveClubsCount() { return activeClubsCount; }
    public AtomicInteger getActiveTeamsCount() { return activeTeamsCount; }
    public AtomicInteger getOnlineUsersCount() { return onlineUsersCount; }
} 