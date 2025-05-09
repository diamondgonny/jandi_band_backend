package com.jandi.band_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// 데이터베이스 자동 구성 비활성화
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class JandiBandBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JandiBandBackendApplication.class, args);
    }

}
