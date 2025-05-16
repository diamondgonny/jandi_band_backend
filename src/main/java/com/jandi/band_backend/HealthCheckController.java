package com.jandi.band_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    private final LocalDateTime startTime = LocalDateTime.now();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/")
    public Map<String, String> getServerStatus() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "running");
        response.put("lastUpdateTime", startTime.format(formatter));
        response.put("serverTime", LocalDateTime.now().format(formatter));

        return response;
    }
}
