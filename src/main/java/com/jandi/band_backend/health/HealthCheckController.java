package com.jandi.band_backend.health;

import com.jandi.band_backend.config.MetricsConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Health Check API")
@RestController
public class HealthCheckController {

    private final Instant startTime = Instant.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    private final MeterRegistry meterRegistry;
    private final MetricsConfig metricsConfig;

    @Autowired
    public HealthCheckController(MeterRegistry meterRegistry, MetricsConfig metricsConfig) {
        this.meterRegistry = meterRegistry;
        this.metricsConfig = metricsConfig;
    }

    @Operation(summary = "서버 상태 확인")
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        // 비즈니스 로직 처리 시간 측정
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UP");
            response.put("uptime", Duration.between(startTime, Instant.now()).toString());
            response.put("serverTime", formatter.format(Instant.now()));
            
            // 추가적인 서버 정보
            response.put("activeUsers", metricsConfig.getActiveUsers().get());
            response.put("totalApiCalls", getTotalApiCalls());
            response.put("systemInfo", getSystemInfo());
            
            return response;
            
        } finally {
            // 비즈니스 로직 처리 시간 기록
            if (metricsConfig.getBusinessLogicTimer() != null) {
                sample.stop(metricsConfig.getBusinessLogicTimer());
            }
        }
    }

    @Operation(summary = "상세 서버 메트릭 정보")
    @GetMapping("/health/metrics")
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // 애플리케이션 메트릭
        metrics.put("activeUsers", metricsConfig.getActiveUsers().get());
        metrics.put("totalApiCalls", getTotalApiCalls());
        
        // 시스템 메트릭
        metrics.put("systemInfo", getSystemInfo());
        metrics.put("jvmInfo", getJvmInfo());
        
        return metrics;
    }

    @Operation(summary = "활성 사용자 수 증가 (테스트용)")
    @GetMapping("/health/users/increment")
    public Map<String, Object> incrementUsers() {
        int newCount = metricsConfig.getActiveUsers().incrementAndGet();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Active users incremented");
        response.put("activeUsers", newCount);
        
        return response;
    }

    @Operation(summary = "활성 사용자 수 감소 (테스트용)")
    @GetMapping("/health/users/decrement")
    public Map<String, Object> decrementUsers() {
        int newCount = metricsConfig.getActiveUsers().decrementAndGet();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Active users decremented");
        response.put("activeUsers", Math.max(0, newCount));
        
        return response;
    }

    private long getTotalApiCalls() {
        if (metricsConfig.getApiCallCounter() != null) {
            return (long) metricsConfig.getApiCallCounter().count();
        }
        return 0;
    }

    private Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        systemInfo.put("availableProcessors", runtime.availableProcessors());
        systemInfo.put("totalMemory", runtime.totalMemory());
        systemInfo.put("freeMemory", runtime.freeMemory());
        systemInfo.put("maxMemory", runtime.maxMemory());
        systemInfo.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        
        return systemInfo;
    }

    private Map<String, Object> getJvmInfo() {
        Map<String, Object> jvmInfo = new HashMap<>();
        
        jvmInfo.put("javaVersion", System.getProperty("java.version"));
        jvmInfo.put("javaVendor", System.getProperty("java.vendor"));
        jvmInfo.put("osName", System.getProperty("os.name"));
        jvmInfo.put("osVersion", System.getProperty("os.version"));
        jvmInfo.put("osArch", System.getProperty("os.arch"));
        
        return jvmInfo;
    }
}
