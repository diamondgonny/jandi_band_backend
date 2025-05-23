package com.jandi.band_backend.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Health Check API", description = "서버 상태 확인 API")
@RestController
public class HealthCheckController {

    private final Instant startTime = Instant.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    @Operation(
        summary = "서버 상태 확인",
        description = "서버의 현재 상태, 가동 시간, 서버 시간을 확인합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "서버 상태 확인 성공",
            content = @Content(schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("uptime", Duration.between(startTime, Instant.now()).toString());
        response.put("serverTime", formatter.format(Instant.now()));
        return response;
    }
}
